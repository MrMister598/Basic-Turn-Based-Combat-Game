import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class TurnBasedGame {
    public static void main(String[] args) {
        new GameUI();
    }
}

// Character class
class Character {
    String name;
    int maxHP, hp, maxMP, mp, defense, dodgeChance;
    String attackName, secondaryAbilityName, specialAbilityName;
    int attackPower, secondaryAbilityPower, specialAbilityPower;
    int secondaryAbilityCost, specialAbilityCost;

    public Character(String name, int maxHP, int maxMP, int defense, int dodgeChance,
                     String attackName, int attackPower, String secondaryAbilityName, int secondaryAbilityPower,
                     int secondaryAbilityCost, String specialAbilityName, int specialAbilityPower, int specialAbilityCost) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.maxMP = maxMP;
        this.mp = maxMP;
        this.defense = defense;
        this.dodgeChance = dodgeChance;
        this.attackName = attackName;
        this.attackPower = attackPower;
        this.secondaryAbilityName = secondaryAbilityName;
        this.secondaryAbilityPower = secondaryAbilityPower;
        this.secondaryAbilityCost = secondaryAbilityCost;
        this.specialAbilityName = specialAbilityName;
        this.specialAbilityPower = specialAbilityPower;
        this.specialAbilityCost = specialAbilityCost;
    }

    public int useAttack() {
        return attackPower;
    }

    public int useSecondaryAbility() {
        if (mp >= secondaryAbilityCost) {
            mp -= secondaryAbilityCost;
            return secondaryAbilityPower;
        }
        return 0;
    }

    public int useSpecialAbility() {
        if (mp >= specialAbilityCost) {
            mp -= specialAbilityCost;
            return specialAbilityPower;
        }
        return 0;
    }

    public void usePotion() {
        this.mp = Math.min(this.mp + 10, maxMP);
    }

    public String getInfo() {
        return name + "\nHP: " + hp + "/" + maxHP + "\nMP: " + mp + "/" + maxMP + "\nDefense: " + defense + "\nDodge: " + dodgeChance + "%";
    }

    public String getBriefInfo() {
        return name + " - HP: " + maxHP + ", MP: " + maxMP;
    }
}

// Player class
class Player {
    Character character;

    public Player(Character character) {
        this.character = character;
    }

    public boolean dodge() {
        return new Random().nextInt(100) < character.dodgeChance;
    }

    public void takeDamage(int damage) {
        int reducedDamage = Math.max(0, damage - character.defense);
        character.hp -= reducedDamage;
    }

    public boolean isAlive() {
        return character.hp > 0;
    }
}

// Game class for managing game logic
class Game {
    Player player1, player2;
    Player currentPlayer;
    Player opponentPlayer;
    JTextArea gameLog;
    JLabel currentPlayerLabel;
    JTextArea player1Stats, player2Stats;

    public Game(Player player1, Player player2, JTextArea gameLog, JLabel currentPlayerLabel, JTextArea player1Stats, JTextArea player2Stats) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.opponentPlayer = player2;
        this.gameLog = gameLog;
        this.currentPlayerLabel = currentPlayerLabel;
        this.player1Stats = player1Stats;
        this.player2Stats = player2Stats;
        updateStats();
        updateCurrentPlayerLabel();
    }

    public void switchTurn() {
        Player temp = currentPlayer;
        currentPlayer = opponentPlayer;
        opponentPlayer = temp;
        updateStats();
        updateCurrentPlayerLabel();
    }

    public void performAttack() {
        int damage = currentPlayer.character.useAttack();
        if (opponentPlayer.dodge()) {
            gameLog.append("\n" + opponentPlayer.character.name + " dodged the attack!");
        } else {
            opponentPlayer.takeDamage(damage);
            gameLog.append("\n" + currentPlayer.character.name + " used " + currentPlayer.character.attackName + " dealing " + damage + " damage!");
        }
        checkGameOver();
    }

    public void performSecondaryAbility() {
        int damage = currentPlayer.character.useSecondaryAbility();
        if (damage > 0) {
            if (opponentPlayer.dodge()) {
                gameLog.append("\n" + opponentPlayer.character.name + " dodged the secondary ability!");
            } else {
                opponentPlayer.takeDamage(damage);
                gameLog.append("\n" + currentPlayer.character.name + " used " + currentPlayer.character.secondaryAbilityName + " dealing " + damage + " damage!");
            }
        } else {
            gameLog.append("\nNot enough MP to use secondary ability!");
        }
        checkGameOver();
    }

    public void performSpecialAbility() {
        int damage = currentPlayer.character.useSpecialAbility();
        if (damage > 0) {
            if (opponentPlayer.dodge()) {
                gameLog.append("\n" + opponentPlayer.character.name + " dodged the special ability!");
            } else {
                opponentPlayer.takeDamage(damage);
                gameLog.append("\n" + currentPlayer.character.name + " used " + currentPlayer.character.specialAbilityName + " dealing " + damage + " damage!");
            }
        } else {
            gameLog.append("\nNot enough MP to use special ability!");
        }
        checkGameOver();
    }

    public void usePotion() {
        currentPlayer.character.usePotion();
        gameLog.append("\n" + currentPlayer.character.name + " used a potion to restore MP.");
        checkGameOver();
    }

    private void checkGameOver() {
        if (!opponentPlayer.isAlive()) {
            gameLog.append("\n" + opponentPlayer.character.name + " has been defeated! " + currentPlayer.character.name + " wins!");
            JOptionPane.showMessageDialog(null, currentPlayer.character.name + " wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            disableButtons();
        }
    }

    private void disableButtons() {
        for (Component component : gameLog.getParent().getParent().getComponents()) {
            if (component instanceof JButton) {
                component.setEnabled(false);
            }
        }
    }

    private void updateStats() {
        player1Stats.setText("Player 1\n" + player1.character.getInfo());
        player2Stats.setText("Player 2\n" + player2.character.getInfo());
    }

    private void updateCurrentPlayerLabel() {
        currentPlayerLabel.setText("Current Turn: " + currentPlayer.character.name);
    }
}

// GameUI class for creating the GUI
class GameUI {
    JFrame frame;
    JTextArea gameLog, player1Stats, player2Stats;
    Game game;
    Player player1, player2;
    Character[] characters;
    JLabel currentPlayerLabel;

    public GameUI() {
        frame = new JFrame("Turn-Based Combat Game");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameLog = new JTextArea();
        gameLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(gameLog);

        player1Stats = new JTextArea();
        player1Stats.setEditable(false);

        player2Stats = new JTextArea();
        player2Stats.setEditable(false);

        currentPlayerLabel = new JLabel("Current Turn: ");

        JButton attackButton = new JButton("Attack");
        JButton secondaryAbilityButton = new JButton("Secondary Ability");
        JButton specialAbilityButton = new JButton("Special Ability");
        JButton potionButton = new JButton("Use Potion");

        JPanel panel = new JPanel();
        panel.add(attackButton);
        panel.add(secondaryAbilityButton);
        panel.add(specialAbilityButton);
        panel.add(potionButton);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.add(player1Stats);
        statsPanel.add(currentPlayerLabel);
        statsPanel.add(player2Stats);

        attackButton.addActionListener(e -> {
            game.performAttack();
            game.switchTurn();
        });

        secondaryAbilityButton.addActionListener(e -> {
            game.performSecondaryAbility();
            game.switchTurn();
        });

        specialAbilityButton.addActionListener(e -> {
            game.performSpecialAbility();
            game.switchTurn();
        });

        potionButton.addActionListener(e -> {
            game.usePotion();
            game.switchTurn();
        });

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(statsPanel, BorderLayout.NORTH);

        setupCharacters();
        setupCharacterSelection();
        frame.setVisible(true);
    }

    private void setupCharacters() {
        characters = new Character[]{
                new Character("Warrior", 100, 30, 10, 10, "Slash", 15, "Power Strike", 25, 10, "Berserk", 40, 20),
                new Character("Mage", 70, 50, 5, 5, "Magic Bolt", 20, "Fireball", 35, 15, "Meteor Strike", 60, 30),
                new Character("Rogue", 80, 25, 8, 20, "Dagger Stab", 12, "Poison Strike", 20, 8, "Shadow Assault", 50, 25),
                new Character("Paladin", 90, 40, 15, 5, "Smite", 12, "Holy Light", 20, 10, "Divine Shield", 35, 18),
        };
    }

    private void setupCharacterSelection() {
        String[] characterNames = new String[characters.length];
        for (int i = 0; i < characters.length; i++) {
            characterNames[i] = characters[i].name;
        }

        String player1Choice = (String) JOptionPane.showInputDialog(
                frame,
                "Player 1, choose your character:",
                "Character Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                characterNames,
                characterNames[0]
        );

        String player2Choice = (String) JOptionPane.showInputDialog(
                frame,
                "Player 2, choose your character:",
                "Character Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                characterNames,
                characterNames[1]
        );

        // Display character info preview if needed
        for (Character character : characters) {
            if (player1Choice.equals(character.name)) {
                if (confirmPreview(character)) {
                    JOptionPane.showMessageDialog(frame, character.getInfo(), "Character Info", JOptionPane.INFORMATION_MESSAGE);
                }
                player1 = new Player(character);
            }
            if (player2Choice.equals(character.name)) {
                if (confirmPreview(character)) {
                    JOptionPane.showMessageDialog(frame, character.getInfo(), "Character Info", JOptionPane.INFORMATION_MESSAGE);
                }
                player2 = new Player(character);
            }
        }

        game = new Game(player1, player2, gameLog, currentPlayerLabel, player1Stats, player2Stats);
    }

    private boolean confirmPreview(Character character) {
        int response = JOptionPane.showConfirmDialog(
                frame,
                "Would you like to preview stats for " + character.name + "?",
                "Preview Character",
                JOptionPane.YES_NO_OPTION
        );
        return response == JOptionPane.YES_OPTION;
    }
}
