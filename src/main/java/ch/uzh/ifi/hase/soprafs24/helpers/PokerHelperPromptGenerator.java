package ch.uzh.ifi.hase.soprafs24.helpers;

import ch.uzh.ifi.hase.soprafs24.constant.GameStatus;
import java.util.List;

public class PokerHelperPromptGenerator {
    // Keep the original template with {n} placeholders for reference
    public static final String PROMPT_TEMPLATE = """
Pretend you're a poker expert helping a beginner with Texas Hold'em. 

You will be given the community cards, player cards, the pot size, amount of money left for the player, the game state (preflop, flop, river, etc) and approximate chance to win derived using 10000 iterations of monte carlo simulation. 

Your job is to help to provide the user with a short and concise guide (<150 words) on how they should play if they are normal risk or high risk (YOLO) gameplay and your logic behind what you recommend. Explain this clearly and concisely. Do not use markdown as the interface does not support it. Use plain text only and be generous with your newline characters so the text is easy to read.

The community cards are %s.
The player's hand is %s.
The pot is %d dollars.
The user has %d dollars left.
The game is in the %s state.
The user has a %s chance to win.

The rules are as follows:

The Deal

Every player is dealt two cards, for their eyes only.

The dealer spreads five cards - three at once, then another, then another - which can be used by all players to make their best possible five-card hand.
The Play

In Hold'em, each player is dealt two private cards (known as 'hole cards') that belong to them alone. Five community cards are dealt face-up, to form the 'board'. All players in the game use these shared community cards in conjunction with their own hole cards to each make their best possible five-card poker hand. In Hold'em, a player may use any combination of the seven cards available to make the best possible five-card poker hand, using zero, one or two of their private hole cards.

The Blinds

In Hold'em, a marker called 'the button' or 'the dealer button' indicates which player is the dealer for the current game. Before the game begins, the player immediately clockwise from the button posts the "small blind", the first forced bet. The player immediately clockwise from the small blind posts the "big blind", which is typically twice the size of the small blind, but the blinds can vary depending on the stakes and betting structure being played.

In Limit games, the big blind is the same as the small bet, and the small blind is typically half the size of the big blind but may be larger depending on the stakes. For example, in a 

        
2/2/

      

4 Limit game the small blind is $1 and the big blind is $2. In a 

        
15/15/

      

30 Limit game, the small blind is $10 and the big blind is $15.

In Pot Limit and No Limit games, the games are referred to by the size of their blinds (for example, a 

        
1/1/

      

2 Hold'em game has a small blind of $1 and a big blind of $2).

Depending on the exact structure of the game, each player may also be required to post an 'ante' (another type of forced bet, usually smaller than either blind, posted by all players at the table) into the pot.

Now, each player receives his or her two hole cards. Betting action proceeds clockwise around the table, starting with the player 'under the gun' (immediately clockwise from the big blind).

Player Betting Options

In Hold'em, as with other forms of poker, the available actions are 'fold', 'check', 'bet', 'call' or 'raise'. Exactly which options are available depends on the action taken by the previous players. If nobody has yet made a bet, then a player may either check (decline to bet, but keep their cards) or bet. If a player has bet, then subsequent players can fold, call or raise. To call is to match the amount the previous player has bet. To raise is to not only match the previous bet, but to also increase it.

Pre-Flop

After seeing his or her hole cards, each player now has the option to play his or her hand by calling or raising the big blind. The action begins to the left of the big blind, which is considered a 'live' bet on this round. That player has the option to fold, call or raise. For example, if the big blind was $2, it would cost $2 to call, or at least $4 to raise. Action then proceeds clockwise around the table.

Betting continues on each betting round until all active players (who have not folded) have placed equal bets in the pot.

The Flop

Now, three cards are dealt face-up on the board. This is known as 'the flop'. In Hold'em, the three cards on the flop are community cards, available to all players still in the hand. Betting on the flop begins with the active player immediately clockwise from the button. The betting options are similar to pre-flop, however if nobody has previously bet, players may opt to check, passing the action to the next active player clockwise.

The Turn

When the betting action is completed for the flop round, the 'turn' is dealt face-up on the board. The turn is the fourth community card in Hold'em (and is sometimes also called 'Fourth Street'). Another round of betting ensues, beginning with the active player immediately clockwise from the button.

The River

When betting action is completed for the turn round, the 'river' or 'Fifth Street' is dealt face-up on the board. The river is the fifth and final community card in a Hold'em game. Betting again begins with the active player immediately clockwise from the button, and the same betting rules apply as they do for the flop and turn, as explained above.

The Showdown

If there is more than one remaining player when the final betting round is complete, the last person to bet or raise shows their cards, unless there was no bet on the final round in which case the player immediately clockwise from the button shows their cards first. The player with the best five-card poker hand wins the pot. In the event of identical hands, the pot will be equally divided between the players with the best hands. Hold'em rules state that all suits are equal.

After the pot is awarded, a new hand of Hold'em is ready to be played. The button now moves clockwise to the next player, blinds and antes are once again posted, and new hands are dealt to each player.
            """;
            
    /**
     * Generates a prompt for the poker helper AI.
     * 
     * @param communityCards List of community cards as strings (e.g. ["AH", "QC", ...])
     * @param playerHand Player's hand as a list of strings
     * @param potSize Size of the pot as an integer
     * @param userMoneyLeft Amount of money the user has left as an integer
     * @param gameStatus Current game status
     * @param chanceToWin User's chance to win as a float
     * @return The formatted prompt string
     */
    public static String generatePrompt(
            List<String> communityCards,
            List<String> playerHand,
            Long potSize,
            Long userMoneyLeft,
            GameStatus gameStatus,
            double chanceToWin) {
        
        // Format the community cards
        String formattedCommunityCards = formatCardList(communityCards);
        
        // Format the player's hand
        String formattedPlayerHand = formatCardList(playerHand);
        
        // Format the game status to lowercase
        String formattedGameStatus = gameStatus.name().toLowerCase();
        
        // Format chance to win as a percentage
        String formattedChanceToWin = String.format("%.1f%%", chanceToWin * 100);
        
        // Apply the formatted values to the template
        return String.format(
                PROMPT_TEMPLATE,
                formattedCommunityCards,
                formattedPlayerHand,
                potSize,
                userMoneyLeft,
                formattedGameStatus,
                formattedChanceToWin
        );
    }
    
    /**
     * Helper method to format a list of cards into a readable string.
     * 
     * @param cards List of card strings
     * @return Formatted string representation
     */
    private static String formatCardList(List<String> cards) {
        if (cards == null || cards.isEmpty()) {
            return "none";
        }
        
        return String.join(", ", cards);
    }
}
