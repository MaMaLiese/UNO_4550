

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static Scanner input = new Scanner(System.in);
    private static PrintStream output = new PrintStream(System.out);
    private static ArrayList<Player> playersInGame;    //players in game list
    private static CardDeck drawPile;   //ziehstapel
    private static CardDeck discardPile;  //ablegestapel
    private Help help = new Help();
    private boolean clockweis = true;      //spielrichtung
    private static int currentPlayerNumber;
    private String winner;
    private boolean gameOver;
    //    private int round = 1;
//    private int session = 1;
//    private boolean exit = false;
    private static String newColor;


    public static String getNewColor() {
        return newColor;
    }

    public static String setNewColor(String newColor) {
        newColor = newColor;
        return newColor;
    }

    public static CardDeck getDiscardPile() {
        return discardPile;
    }

    public static int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int currentPlayerNumber) {
        this.currentPlayerNumber = currentPlayerNumber;
    }

    public Game() {
        //konstruktor
        playersInGame = new ArrayList<>();
        drawPile = new CardDeck();
        discardPile = new CardDeck();
    }


    public void start() {
        // help.printHelp();  //help am anfang anzeigen
        addPlayers();

        shareCards();   //karten austeilen
        layStartCard();  //erste karte auf dem tisch
        chooseInitialPlayer();
        cardChoice();

    }

    public Player chooseInitialPlayer() {
        //wählt die erste spieler random
        Random initialPlayer = new Random();
        int randomIndex = initialPlayer.nextInt(3);

        setCurrentPlayerNumber(randomIndex);

        Player first = playersInGame.get(randomIndex);
        return first;
    }

    public static Player currentPlayer() {
        Player currentPlayer = playersInGame.get(getCurrentPlayerNumber());
        return currentPlayer;
    }

    public void addPlayers() {
        System.out.println("Please enter number of players: ");
        int num = input.nextInt();

        while (num < 1 || num > 4) {
            System.out.println("Max 4 players are allowed. Please choose between 1 and 4");
            num = input.nextInt();
        }
        input.nextLine();
        for (int i = 0; i < num; i++) {
            System.out.println("Enter your name: ");
            String name = input.nextLine();
            Player player = new Player(name, getCurrentPlayerNumber());
            output.println("Hello " + name);
            playersInGame.add(player);
        }
        System.out.println("Number of human players: " + playersInGame.size() + "\n");
        int botSize = 4 - playersInGame.size();
        for (int i = 0; i < botSize; i++) {
            String name = "Bot " + (i + 1);
            Bot bot = new Bot(name, getCurrentPlayerNumber());
            playersInGame.add(bot);
            System.out.println(name + " will be joining you as well");
        }
    }

    public void cardChoice() {
        //prüft ob der player kann eine karte legen und welche, wenn er kann nicht, zieht eine karte
        do {
            Player currentPlayer = currentPlayer();
            output.println("\nPlayer " + currentPlayer.getName() + " your turn");
            penalty();
            if (canPlayerDropACard()) {
                output.println("Your cards: " + "\n" + currentPlayer.showMyCards());
                output.println("Which card do you want to play?");
                discardPile.addToDiscardPile(currentPlayer.playerDropCard());
                output.println("Card on Table: " + discardPile.getDropCard());

            } else {
                output.println("Yout still don't have a card to play");
                output.println("\nCard on Table: " + discardPile.getDropCard() + "\n");
            }
            checkNextTurn();
        } while (gameOver != true);
    }

    public void shareCards() {
        //karten austeilen - 7karte
        for (Player p : playersInGame) {
            for (int i = 0; i < 7; i++) {
                p.giveCard(drawPile.drawCard());  //eine karte von deck zu spieler
            }
        }
    }

    public Card layStartCard() {
        //erste karte auf dem tisch, wenn es ist +4, dann wird die farbe random ausgewählt
        Card card = drawPile.drawCard();
        discardPile.addToDiscardPile(card);

        if (card.getSign().equals("ColorChange") || card.getSign().equals("+4")) {
            // Wenn die erste Karte eine Farbwahlkarte ist, wird die Farbe zufällig ausgewählt
            Random random = new Random();
            String[] colors = {"Red", "Green", "Yellow", "Blue"};
            int randomIndex = random.nextInt(colors.length);
            String startColor = colors[randomIndex];
            setNewColor(startColor);
            output.println("First card is: " + card);
            output.println("First color is: " + startColor);
        } else {
            output.println("First card is: " + card);
        }

        return card;
    }

    public static boolean cardValidation(Card cardOnTheTable) {
        Card discardDeckCard = getDiscardPile().getDropCard();
        Player currentPlayer = currentPlayer();

        if (cardOnTheTable.getColor().equals("Black")) {
            if (cardOnTheTable.getSign().equals("+4") || cardOnTheTable.getSign().equals("ColorChange")) {
                setColorIfColorChangeCard(cardOnTheTable);
                return true;
            }
        } else if (cardOnTheTable.getColor().equals(newColor)) {
            return true;
        } else if (cardOnTheTable.getColor().equals(discardDeckCard.getColor())) {
            return true;
        } else if (cardOnTheTable.getSign().equals(discardDeckCard.getSign())) {
            return true;
        } else {
            output.println("Error... Choose another card!");
            output.println("Card on Table: " + discardDeckCard);
        }

        if (discardDeckCard.getSign().equals("+2") || discardDeckCard.getSign().equals("+4")) {
            if (discardDeckCard.getSign().equals("+2")) {
                penalty();
            } else if (discardDeckCard.getSign().equals("+4")) {
                penalty();
            }
        }

        return false;
    }

    public static void penalty() {
        //prüft, wie viel karte muss ein spieler heben
        Player currentPlayer = currentPlayer();
        Card discardDeckCard = getDiscardPile().getDropCard();

        if (discardDeckCard.getSign().equals("+2")) {
            output.println("But first you have to take 2 cards!");
            drawPenaltyCard();
            drawPenaltyCard();
        } else if (discardDeckCard.getSign().equals("+4")) {
            output.println("But first you have to take 4 cards!");
            drawPenaltyCard();
            drawPenaltyCard();
            drawPenaltyCard();
            drawPenaltyCard();
        } else {
        }
    }

    public void checkNextTurn() {
        //prüft, wer ist die nächste beim reverse, stop und beim normale karte
        Card discardDeckCard = getDiscardPile().getDropCard();
        if (discardDeckCard.getSign().equals("Reverse")) {
            isCardIsReverse();
        } else if (discardDeckCard.getSign().equals("Stop")) {
            isCardStop();
        } else {
            isCardNormal();
        }
    }

    public int isCardNormal() {
        //prüft, ob die karte ist eine normale karte
        int currentPlayerIndex = getCurrentPlayerNumber();

        if (currentPlayerIndex == 0) {
            if (clockweis) {
                currentPlayerIndex++;
            } else {
                currentPlayerIndex = 3;
            }
        } else if (currentPlayerIndex == 3) {
            if (clockweis) {
                currentPlayerIndex = 0;
            } else {
                currentPlayerIndex = 2;
            }
        } else {
            if (clockweis) {
                currentPlayerIndex++;
            } else {
                currentPlayerIndex--;
            }
        }
        setCurrentPlayerNumber(currentPlayerIndex);
        return currentPlayerIndex;
    }

    public int isCardIsReverse() {
        //beim reverse karte prüft wer ist die nächste spieler
        int currentPlayerIndex = getCurrentPlayerNumber();

        if (currentPlayerIndex == 0) {
            if (clockweis) {
                currentPlayerIndex = 3;
                clockweis = false;
            } else {
                currentPlayerIndex = 1;
                clockweis = true;
            }
        } else if (currentPlayerIndex == 3) {
            if (clockweis) {
                currentPlayerIndex = 2;
                clockweis = false;
            } else {
                currentPlayerIndex = 0;
                clockweis = true;
            }
        } else {
            if (clockweis) {
                currentPlayerIndex--;
                clockweis = false;
            } else {
                currentPlayerIndex++;
                clockweis = true;
            }
        }
        setCurrentPlayerNumber(currentPlayerIndex);
        return currentPlayerIndex;
    }

    public int isCardStop() {
        //spielrichtung
        int currentPlayerIndex = getCurrentPlayerNumber();

        if (currentPlayerIndex == 0) {
            if (clockweis) {
                currentPlayerIndex = 2;
            } else {
                currentPlayerIndex = 2;
            }
        } else if (currentPlayerIndex == 3) {
            if (clockweis) {
                currentPlayerIndex = 1;
            } else {
                currentPlayerIndex = 1;
            }
        } else if (currentPlayerIndex == 1) {
            if (clockweis) {
                currentPlayerIndex = 3;
            } else {
                currentPlayerIndex = 3;
            }
        } else {
            if (clockweis) {
                currentPlayerIndex = 0;
            } else {
                currentPlayerIndex = 0;
            }
        }
        setCurrentPlayerNumber(currentPlayerIndex);
        return currentPlayerIndex;
    }

    public static void drawPenaltyCard() {
        //wenn ein spieler bekommt ein +2 oder +4 karte, er muss abheben
        Player currentPlayer = currentPlayer();
        currentPlayer.giveCard(drawPile.drawCard());
    }


    public boolean canPlayerDropACard() {
        //automatisch prüft, kann der spieler eine karte legen, oder muss aufheben
        Card discardDeckCard = getDiscardPile().getDropCard();
        Player currentPlayer = currentPlayer();
        ArrayList<Card> hand = currentPlayer.getCardsInHand();
        boolean hasCard = false;

        for (Card card : hand) {
            if (discardDeckCard.getColor().equals(card.getColor()) || discardDeckCard.getSign().equals(card.getSign())
                    || card.getColor().equals("Black") || card.getColor().equals(getNewColor())) {
                hasCard = true;
                break;
            }
        }

        if (!hasCard) {
            output.println("Sorry, you dont have a card to play. You have to draw a card!");
            drawPenaltyCard();      //wenn der spieler hat keine richtige karte zum legen, zieht automatisch eine karte
            for (Card card : hand) {
                if (discardDeckCard.getColor().equals(card.getColor()) || discardDeckCard.getSign().equals(card.getSign())
                        || card.getColor().equals("Black") || card.getColor().equals(getNewColor())) {
                    hasCard = true;
                    break;
                }
            }
        }
        return hasCard;
    }

    public static void colorChange() {
        //spieler kann farbe wählen
        Card discardDeckCard = getDiscardPile().getDropCard();
        if (discardDeckCard.getSign().equals("ColorChange")) {
            output.println("Give me a new color: ");
            String newColor = input.nextLine().toLowerCase();
            setNewColor(newColor);

        }
    }

    public static void setColorIfColorChangeCard(Card cardOnTheTable) {
        Scanner input = new Scanner(System.in);
        do {
            System.out.println("Choose a color: Red, Blue, Green, Yellow");
            String colorWish = input.nextLine();
            if (colorWish.equalsIgnoreCase("Red")) {
                System.out.println("You wish for Red");
                cardOnTheTable.setColor("Red");
                return;
            } else if (colorWish.equalsIgnoreCase("Blue")) {
                System.out.println("You wish for Blue");
                cardOnTheTable.setColor("Blue");
                return;
            } else if (colorWish.equalsIgnoreCase("Green")) {
                System.out.println("You wish for Green");
                cardOnTheTable.setColor("Green");
                return;
            } else if (colorWish.equalsIgnoreCase("Yellow")) {
                System.out.println("You wish for Yellow");
                cardOnTheTable.setColor("Yellow");
                return;
            } else {
                System.out.println("This color is not valid!");
            }
        } while (true);
    }
    public boolean winner() {
        for (Player p : playersInGame) {
            if (p.getCardsInHand().size() == 0) {
                winner = p.getName();
                gameOver = true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Players=" + playersInGame;
    }
}