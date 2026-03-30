import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/**
 * Minimal Trie sanity-check UI.
 *
 * Uses ONLY these Trie methods:
 *   - insert(String word)
 *   - contains(String word)                 // not required by the UI, but part of the required API
 *   - mostLikelyNextChar(String prefix)
 *   - mostLikelyNextWord(String prefix)
 *
 * How to run:
 *   1) Make sure Trie.java is on the classpath and implements the methods above.
 *   2) (Optional) Provide a training text file as args[0]. The file should contain whitespace-separated words.
 *      Example: java TrieDisplay_minimal words.txt
 *
 * Controls:
 *   - Type letters to build a prefix (the current token after the last space).
 *   - SPACE adds a space to the text (starts a new word).
 *   - BACKSPACE deletes.
 */
public class TrieDisplay_minimal extends JPanel implements KeyListener, MouseListener {

    private Trie trie;

    // Full text the user has typed so far
    private final StringBuilder typed = new StringBuilder();

    // Cached display values (recomputed on each key press)
    private String currentPrefix = "";
    private JComboBox<String> movieSelect;
    private String currentMovie;
    private char nextChar = '_';
    private String nextWord = "";
    //Added fields for feature implementations.
    private boolean recogWord = false;
    private ArrayList<Character> topLetters = new ArrayList<>(); //List of top words / characters that is displayed in the UI
    private ArrayList<String> topWords = new ArrayList<>();
    private ArrayList<Rectangle> wordClickRegion = new ArrayList<>();
    //Rectangle click region set up using mouseListener w/ blue highlighted words.
    //Needed for autocomplete feature.
    private Image background;

    public TrieDisplay_minimal(Trie trie) {
        this.trie = trie;

        setPreferredSize(new Dimension(900, 500));
        setBackground(Color.WHITE);

        setFocusable(true); //Set up mose & key listeners and allow for focus on UI text elements instead of background.
        addKeyListener(this);
        addMouseListener(this);

		/* Set up dropdown w/ all movie titles using actionListener to click on an option
		and load the selected movie script into the Trie. Loads Inception movie by default. */
        updatePredictions();
        String[] movies = {"Inception", "The Dark Knight Rises", "Toy Story", "Iron Man"};
        movieSelect = new JComboBox<>(movies); //Dropdown setup begins here
        movieSelect.setFocusable(false);
        add(movieSelect);
        loadMovie("Inception");
        movieSelect.addActionListener(e -> {
			String selectedMovie = (String) movieSelect.getSelectedItem();
			loadMovie(selectedMovie);
			requestFocusInWindow(); //Gives focus to UI elements (text, click, & dropdown components) to prevent outside interference.
		});
    }

	/* Method is used to load the right movie based on the option selected from dropdown
	by assigning script to fileName, then background image is also saved here for the
	diff. movies. */
    private void loadMovie(String movieName){
		trie = new Trie();
		currentMovie = movieName;
		String fileName = "";
		String selectedImage = "";
		if(movieName.equals("The Dark Knight Rises")){
			fileName = "Batman.txt";
			selectedImage = "batmanImage.jpg";
		}
		else if(movieName.equals("Toy Story")){
			fileName = "ToyStory.txt";
			selectedImage = "toyStoryImage.jpg";
		}
		else if(movieName.equals("Iron Man")){
			fileName = "IronMan.txt";
			selectedImage = "ironManImage.jpg";
		}
		else{
			fileName = "Inception.txt";
			selectedImage = "inceptionImage.jpg";
		}
		loadWordsIntoTrie(trie, fileName);
		background = new ImageIcon(selectedImage).getImage();
		currentPrefix = "";
		//typed.SetLength(0);
		topWords.clear();
		repaint();
	}

	/* mouseClick method is primarily for the autocomplete feature which uses 'box' regions
	to pinpoint where the user clicked in the interactable field and load that selected word
	into the text field. updatePredictions() called in the end. */
	@Override
	public void mouseClicked(MouseEvent e){
		Point currPoint = e.getPoint();
		String selectedWord = "";
		for(int i = 0; i< wordClickRegion.size(); i++){ //Clickable region that encapsulates all UI-interactable words.
			if(wordClickRegion.get(i).contains(currPoint)){
				//Use currPoint to check where user clicked and load the correct word into the text field.
				selectedWord = topWords.get(i);
				int lastSpace = typed.lastIndexOf(" ");
				if(lastSpace >= 0){
					typed.replace(lastSpace + 1, typed.length(),selectedWord + " "); //REplacement
				} else{
					typed.setLength(0);
					typed.append(selectedWord+" ");
				}
				updatePredictions();
				requestFocusInWindow();
				break;
			}
		}
	}
	//Dummy methods that I had to include for the program to run (not important).

	@Override public void mousePressed(MouseEvent e){

	}

	@Override public void mouseReleased(MouseEvent e){

	}

	@Override public void mouseEntered(MouseEvent e){

	}

	@Override public void mouseExited(MouseEvent e){

	}

    /** Recomputes currentPrefix, nextChar, and nextWord from the current typed text. */
    private void updatePredictions() {
        currentPrefix = getCurrentPrefix(typed.toString());
		/* Add the list of top 5 words + characters if a prefix is typed
		onto the text field, or otherwise use '_' to denote when nothing is typed. */
        if (currentPrefix.isEmpty()) {
            topLetters.add('_');
            topWords.add("_");
        } else {
            topLetters = trie.top5Letters(currentPrefix);
            topWords = trie.top5Words(currentPrefix);
        }


        if(!currentPrefix.isEmpty()){
			recogWord = trie.contains(currentPrefix);
		} else{
			/* This snippet makes sure that autocomplete works properly because we exclude
			the space inserted at the end of displaying an autocomplete word to make sure
			that we can properly check if word exists or not. */
			String text = typed.toString().trim();
			int spaceAtEnd = text.lastIndexOf(' ');
			String endWord = "";
			if(spaceAtEnd >= 0){
				endWord = text.substring(spaceAtEnd + 1);
			} else{
				endWord = text;
			}

			if(!endWord.isEmpty()){
				recogWord = trie.contains(endWord);
			} else{
				recogWord = false;
			}
		}
        repaint();
    }

    /** Returns the current "token" after the last space (letters only, lowercased). */
    private static String getCurrentPrefix(String text) {
        int lastSpace = text.lastIndexOf(' ');
        String token = (lastSpace >= 0) ? text.substring(lastSpace + 1) : text;

        // Keep it simple: letters only
        token = token.replaceAll("[^A-Za-z]", "").toLowerCase();
        return token;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(background != null){
			g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
		}
		g.setColor(new Color(255,255,255,180));
		g.fillRect(0,0,getWidth(),getHeight());

		// NOTE: a lot of these hardcoded values work relative to one another
		//       change with caution and ensure they work together
        g.setFont(new Font("Consolas", Font.PLAIN, 18));

        int x = 30;
        int y = 40;


        g.setColor(Color.BLACK);
        g.drawString("Typed text:", x, y);
        y += 26;

        // Draw the typed text (very simple wrap)
        String[] lines = wrap(typed.toString(), 80);
        for (String line : lines) {
            g.drawString(line, x, y);
            y += 22;
        }

        y += 18;
        g.drawLine(x, y, x + 820, y);
        y += 30;

        g.setColor(Color.DARK_GRAY);
        g.drawString("Current prefix: " + (currentPrefix.isEmpty() ? "(none)" : currentPrefix), x, y);
        y += 28;

        g.drawString("Top 5 Next Letters: " + topLetters.toString(), x, y);
        y += 28;

        g.drawString("Top 5 Next Words: " + topWords.toString(), x, y);
        y += 28;
        wordClickRegion.clear();
        int wordXPos = x;
        for(String word : topWords){
			g.setColor(Color.BLUE);
			g.drawString(word, wordXPos, y);
			int width = g.getFontMetrics().stringWidth(word);
			int height = g.getFontMetrics().getHeight();
			Rectangle rect = new Rectangle(wordXPos, y - height + 5, width, height);
			wordClickRegion.add(rect);
			wordXPos += (width + 20);
		}
		g.setColor(Color.BLACK);
		y += 30;


        if(recogWord == true){ //Checks the typed prefix against dataset in another space (recogWord --> true or false) and displays the result in these conditions using g.drawString()
			g.setColor(Color.GREEN);
			g.drawString("Word exists!", x, y);
			y += 40;
		} else{
			g.setColor(Color.RED);
			g.drawString("Word does not exist.", x, y);
			y += 40;
		}

        g.setColor(Color.GRAY);
        g.drawString("Tip: type a prefix (letters). Backspace deletes. Space starts a new word.", x, y);
        g.drawString("Autocomplete Feature: Click words in the top 5 words list to autocomplete.", x, y+30);
    }

    /** Very small, dumb word wrap for display only. */
    private static String[] wrap(String s, int maxChars) {
        if (s.length() <= maxChars) return new String[]{s};

        ArrayList<String> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            int end = Math.min(i + maxChars, s.length());
            out.add(s.substring(i, end));
            i = end;
        }
        return out.toArray(new String[0]);
    }

    // ---- KeyListener --------------------------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();

        if (c == KeyEvent.CHAR_UNDEFINED) return;

        // Backspace is handled in keyPressed (more reliable)
        if (c == '\b') return;

        // Allow space OR treat enter / return like a space
        if (c == ' ' || c == '\n' || c == '\r') {
            typed.append(' ');
            updatePredictions();
            return;
        }

        // Allow any non-control character
        // (things that are not like tabs, return, etc..)
        if (!Character.isISOControl(c)) {
            typed.append(c);
            updatePredictions();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Backspace here
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (typed.length() > 0) {
                typed.deleteCharAt(typed.length() - 1);
                updatePredictions();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // not used (included to complete interface)
    }

    /*********  Main *********************/

    public static void main(String[] args) {
        Trie trie = new Trie();
        int numWords = loadWordsIntoTrie(trie,"Inception.txt");
        System.out.println("Loaded "+String.format("%,d", numWords)+" words from Inception (2010).");

        JFrame frame = new JFrame("TrieDisplay (Minimal)");
        TrieDisplay_minimal panel = new TrieDisplay_minimal(trie);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Give the panel focus so it receives key events
        SwingUtilities.invokeLater(panel::requestFocusInWindow);
    }

   private static int loadWordsIntoTrie(Trie trie, String filename) {
       int count = 0;

       try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
           String line = "";
           while ((line = br.readLine()) != null) {

               // Normalize line: lowercase, letters only, spaces preserved
               line = line.replaceAll("[^A-Za-z]", " ").toLowerCase();

               // Split into words
               String[] words = line.split("\\s+");

               for (String w : words) {
                   if (!w.isEmpty()) {
                       trie.insert(w);
                       count++;
                   }
               }
           }

       } catch (IOException e) {
           System.out.println("Could not read file: " + filename);
       }

       return count;
   }

}