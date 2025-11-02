#!/usr/bin/env python3
"""
Script to convert raw changelog format to organized markdown format
"""

import re
import sys
import os

def load_pokemon_names(pokemon_csv_path):
    """Load Pokemon names from CSV file."""
    pokemon_names = set()
    try:
        with open(pokemon_csv_path, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                parts = line.split('|')
                if len(parts) > 1:
                    pokemon_name = parts[1].strip()
                    if pokemon_name and pokemon_name != 'Name':  # Skip header
                        pokemon_names.add(pokemon_name.lower())
    except FileNotFoundError:
        print(f"Warning: Pokemon CSV file not found at {pokemon_csv_path}")
    return pokemon_names

def load_trainer_names(trainers_csv_path):
    """Load Trainer names from CSV file."""
    trainer_names = set()
    try:
        with open(trainers_csv_path, 'r', encoding='utf-8') as f:
            for line in f:
                line = line.strip()
                if not line:
                    continue
                parts = line.split('|')
                if len(parts) > 1:
                    trainer_name = parts[1].strip()
                    if trainer_name and trainer_name != 'Name':  # Skip header
                        trainer_names.add(trainer_name.lower())
    except FileNotFoundError:
        print(f"Warning: Trainer CSV file not found at {trainers_csv_path}")
    return trainer_names

def load_move_names_from_java():
    """Extract move names from Java enum file."""
    move_names = set()
    
    # Basic move names from common patterns
    basic_moves = [
        'tackle', 'scratch', 'pound', 'ember', 'water gun', 'razor leaf',
        'thunder', 'earthquake', 'psychic', 'flamethrower', 'ice beam',
        'surf', 'fly', 'cut', 'strength', 'rock smash', 'flash',
        'thunderbolt', 'dragon pulse', 'shadow ball', 'energy ball'
    ]
    
    for move in basic_moves:
        move_names.add(move.lower())
    
    return move_names

def categorize_entry_by_names(entry, pokemon_names, trainer_names, move_names):
    """Categorize entry based on Pokemon, Trainer, and Move name detection."""
    first_line = entry.split('\n')[0].lower()
    
    # Check for trainer names first (highest priority)
    for trainer_name in trainer_names:
        if trainer_name in first_line:
            return "Trainer Changes"
    
    # Check for Pokemon names second
    for pokemon_name in pokemon_names:
        if pokemon_name in first_line:
            return "Pokemon Changes"
    
    # Check for move names last
    for move_name in move_names:
        if move_name in first_line:
            return "Move Changes"
    
    return None

def convert_changelog(input_file, output_file, version, pokemon_csv_path=None, trainers_csv_path=None):
    """Convert raw changelog to categorized markdown format."""
    
    # Load game data
    pokemon_names = set()
    trainer_names = set()
    move_names = set()
    
    if pokemon_csv_path and os.path.exists(pokemon_csv_path):
        pokemon_names = load_pokemon_names(pokemon_csv_path)
    
    if trainers_csv_path and os.path.exists(trainers_csv_path):
        trainer_names = load_trainer_names(trainers_csv_path)
    
    move_names = load_move_names_from_java()
    
    # Define patterns for categorizing changes
    patterns = {
        "New Features": [
            r"^(Added|ADDED|Created|CREATED|Implemented|IMPLEMENTED)",
            r"^New ",
            r"signature ability",
            r"NUZLOCKE MODE",
            r"new sprites",
            r"sprites for",
            r"sprite for",
            r"^Sprite ",
            r"overworld sprites"
        ],
        "Feature Updates": [
            r"^(Changed|CHANGED|Updated|UPDATED|Tweaked|TWEAKED|Reworked|REWORKED|Revamped|REVAMPED)",
            r"^(Made|MADE)",
            r"^(Moved|MOVED|Gave|GAVE)",
            r"^(Buffed|BUFFED|Nerfed|NERFED)",
            r"^(Removed|REMOVED).*compatibility",
            r"^(Removed|REMOVED).*TM",
            r"compatibility",
            r"description",
            r"^Replaced",
            r"rebalanced",
            r"redistributed"
        ],
        "Bug Fixes": [
            r"^(Fixed|FIXED)",
            r"^Finally fixed",
            r"bug",
            r"crash",
            r"issue",
            r"oops",
            r"^Removed.*bug",
            r"^Removed.*issue"
        ]
    }
    
    with open(input_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Split into lines but preserve structure
    lines = content.split('\n')
    
    # Process lines to handle lists
    processed_lines = []
    i = 0
    while i < len(lines):
        line = lines[i].strip()
        if not line or line.startswith('#'):
            i += 1
            continue
            
        # Check if this line ends with a colon and might start a list
        if line.endswith(':') and i + 1 < len(lines):
            # Look ahead to see if next lines are list items
            list_items = []
            j = i + 1
            while j < len(lines) and lines[j].strip().startswith('-'):
                list_items.append(lines[j].strip())
                j += 1
            
            if list_items:
                # This is a list - combine the header with all items
                full_entry = line + '\n' + '\n'.join(list_items)
                processed_lines.append(full_entry)
                i = j  # Skip past all the list items
            else:
                processed_lines.append(line)
                i += 1
        else:
            processed_lines.append(line)
            i += 1
    
    # Categorize changes
    categories = {
        "New Features": [],
        "Feature Updates": [],
        "Bug Fixes": [],
        "Move Changes": [],
        "Pokemon Changes": [],
        "Trainer Changes": [],
        "Uncategorized": []
    }
    
    for entry in processed_lines:
        if not entry:
            continue
            
        # For multi-line entries, use the first line for categorization
        first_line = entry.split('\n')[0]
        categorized = False
        
        # First try name-based categorization
        if pokemon_names or trainer_names or move_names:
            name_category = categorize_entry_by_names(entry, pokemon_names, trainer_names, move_names)
            if name_category:
                categories[name_category].append(entry)
                categorized = True
        
        # If not categorized by names, use pattern matching
        if not categorized:
            for category, category_patterns in patterns.items():
                for pattern in category_patterns:
                    if re.search(pattern, first_line, re.IGNORECASE):
                        categories[category].append(entry)
                        categorized = True
                        break
                if categorized:
                    break
        
        # Additional heuristics for uncategorized items
        if not categorized:
            # Check for specific game terms
            if any(term in first_line.lower() for term in ['pokemon', 'pokémon', 'trainer', 'gym', 'battle']):
                if any(term in first_line.lower() for term in ['fix', 'correct', 'bug', 'crash', 'issue']):
                    categories["Bug Fixes"].append(entry)
                elif any(term in first_line.lower() for term in ['new', 'add', 'implement', 'creat']):
                    categories["New Features"].append(entry)
                else:
                    categories["Feature Updates"].append(entry)
                categorized = True
            
            # Check for move-related changes
            elif any(term in first_line.lower() for term in ['move', 'attack', 'ability']):
                if 'fix' in first_line.lower():
                    categories["Bug Fixes"].append(entry)
                else:
                    categories["Move Changes"].append(entry)
                categorized = True
        
        # Final fallback categorization
        if not categorized:
            if re.search(r'^(Fix|Correct|Repair)', first_line, re.IGNORECASE):
                categories["Bug Fixes"].append(entry)
            elif re.search(r'^(Add|New|Creat|Implement)', first_line, re.IGNORECASE):
                categories["New Features"].append(entry)
            elif re.search(r'^(Chang|Updat|Modif|Adjust|Tweak)', first_line, re.IGNORECASE):
                categories["Feature Updates"].append(entry)
            else:
                categories["Uncategorized"].append(entry)
    
    # Generate markdown
    markdown_content = []
    
    # Add version header
    markdown_content.append(f"# Version {version} Changelog")
    markdown_content.append("")
    
    # Define the order we want categories to appear
    category_order = ["New Features", "Feature Updates", "Bug Fixes", "Move Changes", "Pokemon Changes", "Trainer Changes", "Uncategorized"]
    
    for category in category_order:
        changes = categories[category]
        if changes:  # Only include categories that have changes
            markdown_content.append("---")
            markdown_content.append(f"## {category}")
            for change in changes:
                # Handle multi-line entries
                if '\n' in change:
                    lines = change.split('\n')
                    # First line becomes the main bullet point
                    markdown_content.append(f"- {lines[0]}")
                    # Subsequent lines become sub-bullets
                    for line in lines[1:]:
                        if line.startswith('-'):
                            markdown_content.append(f"    {line}")  # Indent sub-items
                        else:
                            markdown_content.append(f"    - {line}")
                else:
                    markdown_content.append(f"- {change}")
            markdown_content.append("")  # Empty line between categories
    
    # Write to output file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(markdown_content))
    
    print(f"Converted changelog for version {version}")
    print(f"Input: {input_file}")
    print(f"Output: {output_file}")
    
    # Print statistics
    total_entries = sum(len(changes) for changes in categories.values())
    stats = {cat: len(changes) for cat, changes in categories.items() if changes}
    print(f"Total entries: {total_entries}")
    print("Category distribution:", stats)
    
    if categories["Uncategorized"]:
        print(f"\nUncategorized entries ({len(categories['Uncategorized'])}):")
        for entry in categories["Uncategorized"][:5]:  # Show first 5
            print(f"  - {entry.split(chr(10))[0]}")  # Show just first line
        if len(categories["Uncategorized"]) > 5:
            print(f"  ... and {len(categories['Uncategorized']) - 5} more")

if __name__ == "__main__":
    # Check if version was provided as command line argument
    if len(sys.argv) > 1:
        version = sys.argv[1]
    else:
        version = input("Enter version (e.g., 0.8.0-alpha): ")
    
    # Define paths
    base_path = "D:\\Documents\\git\\PokemonXhenos\\res\\info"
    pokemon_csv_path = os.path.join(base_path, "pokemon.csv")
    trainers_csv_path = os.path.join(base_path, "trainers.csv")
    
    # Create output directory if it doesn't exist
    os.makedirs("changelogs", exist_ok=True)
    
    convert_changelog(
        "changelog.txt", 
        f"changelogs/changelog_{version}.md", 
        version,
        pokemon_csv_path,
        trainers_csv_path
    )