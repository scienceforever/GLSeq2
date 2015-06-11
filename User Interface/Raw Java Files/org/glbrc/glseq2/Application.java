package org.glbrc.glseq2;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public final class Application {
  /*
   * Various constants relating to the program itself
   */
  private static final String PROGRAM_NAME = "GLSeq2 User Interface";
  /*
   * Consistent fond scheming
   */
  public static final Font HEADER_FONT = new Font("Courier", Font.PLAIN, 26);
  public static final Font TEXT_FONT = new Font("Courier", Font.PLAIN, 11);
  // Used to write updates to the panel in the UI
  public static final JTextPane txtCurrentUpdates = new JTextPane();
  private final JScrollPane scrollPane = new JScrollPane();

  // Holds the page
  private final JFrame frame = new JFrame();
  // Data storage classes with functions to generate attribute files
  // And config saves
  public static Attributes att;
  public static Run run;
  // Script that runs the GLSeq.top.R script
  private ScriptTask startGlseq;
  /*
   * Most naming conventions I use are standard for SWING. One exception is that
   * text fields are distinguished between text expected to be constant (txtc
   * prefix) and text that may change throughout use (txt prefix) If there is an
   * h after the prefix (lower case) it is a header.
   */
  // The location of the newly generated or input attribute file
  private final JTextPane txtcAttributeFilePath = new JTextPane();
  private final JTextArea txtAttributeFile = new JTextArea();
  //
  private final JPanel panel = new JPanel();
  private final JPanel runContainer = new JPanel();
  private final JTextArea txtRunName = new JTextArea();
  private final JTextPane txtchAttributeFileTitle = new JTextPane();
  private final JPanel runOptionsContainer = new JPanel();
  private final JTextPane txtcRunName = new JTextPane();
  private final JTextPane txtchRunningUpdates = new JTextPane();
  private final JPanel runOptionsTitleContainer = new JPanel();
  private final JTextPane txtchRunOptions = new JTextPane();
  private final JPanel attributeFileContainer = new JPanel();
  // Various buttons associated with subsets of the main page
  private final JButton btnDataAndLibrary = new JButton("Data Sources");
  private final JButton btnPipeline = new JButton("Algorithms and Reference");
  private final JButton btnProcessing = new JButton("Trimming and Processing");
  private final JButton btnEnvironment = new JButton("Environment");
  private final JButton btnGenerateAttributeFile = new JButton("Generate Attribute File");
  private final JButton btnDatabase = new JButton("Updating from Database");
  private final JButton btnPreProcessing = new JButton("Pre-Processing Data");
  private final JButton btnAlignment = new JButton("Aligning");
  private final JButton btnCounting = new JButton("Counting");
  private final JButton btnCollecting = new JButton("Collecting Results");
  private final JButton btnBackground = new JButton("Running in the Background");
  private final JButton btnRun = new JButton("Run");
  // Allows for text styling, mainly centering
  private final SimpleAttributeSet center = new SimpleAttributeSet();

  /**
   * Launch the application.
   */
  public static final void main(String[] args) {
    att = new Attributes();
    run = new Run();
    if (args.length > 0) {
      System.out.println("Generating attribute file from command line arguments.");
      att.setAttributse(args);
      try {
        att.writeAttributesFile();
      } catch (IOException e) {
        System.out.println("Error constructing attribute file");
      }
      // Exit program
      System.out.println("Program is now exiting.");
      return;
    }
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        Application window = new Application();
        window.frame.setVisible(true);
        /**
         * Setting the default UI theme to Nimbus which is a nice looking and
         * general applicable theme package.
         * 
         */
        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
          if (info.getClassName().contains("Nimbus")) {
            try {
              UIManager.setLookAndFeel(info.getClassName());
            } catch (ClassNotFoundException e) {
              System.out.println("The Nimbus theme class has not been found");
            } catch (InstantiationException e) {
              System.out.println("The Nimbus theme class can not be instantiated");
            } catch (IllegalAccessException e) {
              System.out.println("The Nimbus theme class cannot be legally accessed");
            } catch (UnsupportedLookAndFeelException e) {
              System.out.println("The Nimbus theme class is not supported");
            }
            // Once found, peace out
            break;
          }
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public Application() {
    // Makes it so we can center items
    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    /*
     * Both the attribute and run class should save a text file after each run.
     * This calls functions within those classes that loads up the previous data
     * to the user.
     */
    try {
      att.setAttributes();
      updating("Loaded previous attribute configurations from file.");
    } catch (NullPointerException e) {
      updating("No previous attribute file configuration file loaded.");
    }
    try {
      run.updateFromConfig();
      updating("Loaded previous run configurations from file.");
    } catch (NullPointerException e) {
      updating("No previous run file configuration file loaded.");
    }
    initialize();
    /*
     * All of the buttons related to the run file (And in general honestly) work
     * by checking the run class for certain traits and deciding which text to
     * display based on this.
     * 
     * Enums are derived from the ButtonEnum class
     */
    if (run.getUpdateFromDatabase().equals(ButtonEnums.Attribute.UPDATE.value)) {
      btnDatabase.setText(ButtonEnums.AttributeButton.UPDATE.value);
    } else {
      btnDatabase.setText(ButtonEnums.AttributeButton.NO_UPDATE.value);
    }
    //
    if (run.getProcessedData().equals(ButtonEnums.Attribute.PREPROCESSING.value)) {
      btnPreProcessing.setText(ButtonEnums.AttributeButton.PREPROCESSING.value);
    } else {
      btnPreProcessing.setText(ButtonEnums.AttributeButton.NO_PREPROCESSING.value);
    }
    //
    if (run.getAlignment().equals(ButtonEnums.Attribute.ALIGNMENT.value)) {
      btnAlignment.setText(ButtonEnums.AttributeButton.ALIGNMENT.value);
    } else {
      btnAlignment.setText(ButtonEnums.AttributeButton.NO_ALIGNMENT.value);
    }
    //
    if (run.getCounting().equals(ButtonEnums.Attribute.COUNT.value)) {
      btnCounting.setText(ButtonEnums.AttributeButton.COUNT.value);
    } else {
      btnCounting.setText(ButtonEnums.AttributeButton.NO_COUNT.value);
    }
    //
    if (run.getCollectResults().equals(ButtonEnums.Attribute.COLLECT.value)) {
      btnCollecting.setText(ButtonEnums.AttributeButton.COLLECT.value);
    } else {
      btnCollecting.setText(ButtonEnums.AttributeButton.NO_COLLECT.value);
    }
    //
    if (run.getAmpersand().equals(ButtonEnums.Attribute.BACKGROUND.value)) {
      btnBackground.setText(ButtonEnums.AttributeButton.BACKGROUND.value);
    } else {
      btnBackground.setText(ButtonEnums.AttributeButton.NO_BACKGROUND.value);
    }
    //
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    /*
     * Basic settings regarding the frame, including size, the fact that it
     * isn't resizable, and the title.
     */
    frame.setResizable(false);
    frame.setBounds(100, 100, 1101, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setTitle(PROGRAM_NAME);
    frame.getContentPane().setLayout(null);
    /*
     * These are the attribute file option buttons and containers
     * 
     * All of the attribute file buttons simply open a new window containing all
     * the options. This replaces the previous method of reloading the page and
     * (I think) should be faster over longer periods of use.
     */
    attributeFileContainer.setBackground(Color.GRAY);
    attributeFileContainer.setBounds(0, 0, 367, 572);
    frame.getContentPane().add(attributeFileContainer);
    attributeFileContainer.setLayout(null);

    btnDataAndLibrary.setBounds(33, 43, 300, 70);
    attributeFileContainer.add(btnDataAndLibrary);
    btnDataAndLibrary.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Data library = new Data();
        library.setVisible(true);
      }
    });
    btnPipeline.setBounds(33, 124, 300, 70);
    attributeFileContainer.add(btnPipeline);
    btnPipeline.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        RunningOptions running = new RunningOptions();
        running.setVisible(true);
      }
    });
    btnProcessing.setBounds(33, 205, 300, 70);
    attributeFileContainer.add(btnProcessing);
    btnProcessing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Processing processing = new Processing();
        processing.setVisible(true);
      }
    });
    btnEnvironment.setBounds(33, 285, 300, 70);
    attributeFileContainer.add(btnEnvironment);
    btnEnvironment.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Environment environment = new Environment();
        environment.setVisible(true);
      }
    });
    JPanel attributeFileTitleContainer = new JPanel();
    attributeFileTitleContainer.setBackground(Color.GRAY);
    attributeFileTitleContainer.setBounds(0, 0, 367, 70);
    attributeFileContainer.add(attributeFileTitleContainer);
    attributeFileTitleContainer.setLayout(null);

    StyledDocument doc = txtchAttributeFileTitle.getStyledDocument();
    doc.setParagraphAttributes(0, doc.getLength(), center, false);

    txtchAttributeFileTitle.setFont(HEADER_FONT);
    txtchAttributeFileTitle.setForeground(Color.WHITE);
    txtchAttributeFileTitle.setBackground(Color.GRAY);
    txtchAttributeFileTitle.setText("Attribute File");
    txtchAttributeFileTitle.setEditable(false);
    txtchAttributeFileTitle.setBounds(10, 5, 347, 48);
    attributeFileTitleContainer.add(txtchAttributeFileTitle);
    btnGenerateAttributeFile.setBounds(33, 463, 300, 106);
    attributeFileContainer.add(btnGenerateAttributeFile);
    btnGenerateAttributeFile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        try {
          att.saveConfigFile(null);
          String path = att.writeAttributesFile();
          run.setAttributeFilePath(path);
          txtAttributeFile.setText(path);
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    });

    txtAttributeFile.setBounds(33, 396, 298, 56);
    attributeFileContainer.add(txtAttributeFile);
    txtAttributeFile.setWrapStyleWord(true);
    txtAttributeFile.setLineWrap(true);
    txtAttributeFile.setFont(TEXT_FONT);

    txtcAttributeFilePath.setBounds(135, 366, 91, 19);
    attributeFileContainer.add(txtcAttributeFilePath);
    txtcAttributeFilePath.setText("Attribute File Path");
    txtcAttributeFilePath.setForeground(Color.WHITE);
    txtcAttributeFilePath.setFont(TEXT_FONT);
    txtcAttributeFilePath.setEditable(false);
    txtcAttributeFilePath.setBackground(Color.GRAY);

    /*
     * This is where the run option buttons and text boxes are held
     */
    runOptionsContainer.setBackground(Color.GRAY);
    runOptionsContainer.setBounds(367, 40, 367, 532);
    frame.getContentPane().add(runOptionsContainer);
    runOptionsContainer.setLayout(null);
    btnDatabase.setBounds(10, 0, 347, 70);
    btnDatabase.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnDatabase.getText().equals(ButtonEnums.AttributeButton.UPDATE.value)) {
          btnDatabase.setText(ButtonEnums.AttributeButton.NO_UPDATE.value);
          run.setUpdateFromDatabase(ButtonEnums.Attribute.NO_UPDATE.value);
        } else {
          btnDatabase.setText(ButtonEnums.AttributeButton.UPDATE.value);
          run.setUpdateFromDatabase(ButtonEnums.Attribute.UPDATE.value);
        }
      }
    });
    runOptionsContainer.add(btnDatabase);
    btnPreProcessing.setBounds(10, 81, 347, 70);
    btnPreProcessing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnPreProcessing.getText().equals(ButtonEnums.AttributeButton.PREPROCESSING.value)) {
          btnPreProcessing.setText(ButtonEnums.AttributeButton.NO_PREPROCESSING.value);
          run.setProcessedData(ButtonEnums.Attribute.NO_PREPROCESSING.value);
        } else {
          btnPreProcessing.setText(ButtonEnums.AttributeButton.PREPROCESSING.value);
          run.setProcessedData(ButtonEnums.Attribute.PREPROCESSING.value);
        }
      }
    });
    runOptionsContainer.add(btnPreProcessing);
    btnAlignment.setBounds(10, 162, 347, 70);
    btnAlignment.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnAlignment.getText().equals(ButtonEnums.AttributeButton.ALIGNMENT.value)) {
          btnAlignment.setText(ButtonEnums.AttributeButton.NO_ALIGNMENT.value);
          run.setAlignment(ButtonEnums.Attribute.NO_ALIGNMENT.value);
        } else {
          btnAlignment.setText(ButtonEnums.AttributeButton.ALIGNMENT.value);
          run.setAlignment(ButtonEnums.Attribute.ALIGNMENT.value);
        }
      }
    });
    runOptionsContainer.add(btnAlignment);
    btnCounting.setBounds(10, 243, 347, 70);
    btnCounting.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnCounting.getText().equals(ButtonEnums.AttributeButton.COUNT.value)) {
          btnCounting.setText(ButtonEnums.AttributeButton.NO_COUNT.value);
          run.setCounting(ButtonEnums.Attribute.NO_COUNT.value);
        } else {
          btnCounting.setText(ButtonEnums.AttributeButton.COUNT.value);
          run.setCounting(ButtonEnums.Attribute.COUNT.value);
        }
      }
    });
    runOptionsContainer.add(btnCounting);
    btnCollecting.setBounds(10, 324, 347, 70);
    btnCollecting.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnCollecting.getText().equals(ButtonEnums.AttributeButton.COLLECT.value)) {
          btnCollecting.setText(ButtonEnums.AttributeButton.NO_COLLECT.value);
          run.setCollectResults(ButtonEnums.Attribute.NO_COLLECT.value);
        } else {
          btnCollecting.setText(ButtonEnums.AttributeButton.COLLECT.value);
          run.setCollectResults(ButtonEnums.Attribute.COLLECT.value);
        }
      }
    });
    runOptionsContainer.add(btnCollecting);
    btnBackground.setBounds(10, 405, 347, 70);
    btnBackground.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (btnBackground.getText().equals(ButtonEnums.AttributeButton.BACKGROUND.value)) {
          btnBackground.setText(ButtonEnums.AttributeButton.NO_BACKGROUND.value);
          run.setAmpersand(ButtonEnums.Attribute.NO_BACKGROUND.value);
        } else {
          btnBackground.setText(ButtonEnums.AttributeButton.BACKGROUND.value);
          run.setAmpersand(ButtonEnums.Attribute.BACKGROUND.value);
        }
      }
    });
    runOptionsContainer.add(btnBackground);

    txtRunName.setBounds(111, 495, 246, 19);
    runOptionsContainer.add(txtRunName);
    txtRunName.setFont(TEXT_FONT);

    txtcRunName.setBounds(10, 495, 91, 19);
    runOptionsContainer.add(txtcRunName);
    txtcRunName.setForeground(Color.WHITE);
    txtcRunName.setBackground(Color.GRAY);
    txtcRunName.setText("Unique Run Name");
    txtcRunName.setFont(TEXT_FONT);
    txtcRunName.setEditable(false);

    runContainer.setBackground(Color.GRAY);
    runContainer.setBounds(734, 0, 361, 572);
    frame.getContentPane().add(runContainer);
    runContainer.setLayout(null);
    panel.setBackground(Color.LIGHT_GRAY);
    panel.setBounds(10, 40, 341, 440);
    /*
     * 
     * The run button and the constantly updating text box scroll thing are
     * housed under here
     */
    runContainer.add(panel);
    panel.setLayout(null);
    scrollPane.setBounds(10, 11, 321, 418);

    panel.add(scrollPane);
    txtCurrentUpdates.setFont(TEXT_FONT);
    txtCurrentUpdates.setEditable(false);

    scrollPane.setViewportView(txtCurrentUpdates);
    btnRun.setBounds(10, 491, 341, 70);
    btnRun.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent buttonAction) {
        if (txtRunName.getText().length() > 0) {
          if (txtAttributeFile.getText().length() > 0) {
            run.setAttributeFilePath(txtAttributeFile.getText());
            run.setRunId(txtRunName.getText());
            try {
              run.saveConfigFile("RunConfig" + run.getRunId() + ".txt");
              att.saveConfigFile("AttributeConfig" + run.getRunId() + ".txt");
            } catch (IOException error) {
              System.out.println("Problem saving run and attribute configuration files.");
            }
            txtCurrentUpdates.setEnabled(true);
            updating("Now running script with arguments: " + String.valueOf(run.returnArgs()));
            startGlseq = new ScriptTask();
            startGlseq.execute();
          } else {
            updating("Please either generate a new attribute file or enter a path "
                + "to a previously generated file.");
          }
        } else {
          updating("Please name your run in the textbox to the left of the run button");
        }
      }
    });
    runContainer.add(btnRun);
    txtchRunningUpdates.setText("Running Updates");
    StyledDocument docs = txtchRunningUpdates.getStyledDocument();
    docs.setParagraphAttributes(0, docs.getLength(), center, false);
    txtchRunningUpdates.setForeground(Color.WHITE);
    txtchRunningUpdates.setFont(HEADER_FONT);
    txtchRunningUpdates.setEditable(false);
    txtchRunningUpdates.setBackground(Color.GRAY);
    txtchRunningUpdates.setBounds(10, 5, 341, 48);
    runContainer.add(txtchRunningUpdates);
    runOptionsTitleContainer.setBounds(367, 0, 379, 213);
    frame.getContentPane().add(runOptionsTitleContainer);
    runOptionsTitleContainer.setBackground(Color.GRAY);
    runOptionsTitleContainer.setLayout(null);
    txtchRunOptions.setBounds(10, 5, 347, 48);
    runOptionsTitleContainer.add(txtchRunOptions);
    txtchRunOptions.setParagraphAttributes(center, false);
    txtchRunOptions.setFont(HEADER_FONT);
    txtchRunOptions.setForeground(Color.WHITE);
    txtchRunOptions.setBackground(Color.GRAY);
    txtchRunOptions.setText("Run Options");
    txtchRunOptions.setEditable(false);
  }

  /**
   * Allows for various portions of the program to update the text data.
   * 
   * <p>
   * This method will return immediately, adding any information that it knows
   * to the update screen.
   * 
   * @author Michael Lampe
   * @param update
   *          - The value to add to the display
   * 
   */
  public static final void updating(String update) {
    String current = txtCurrentUpdates.getText();
    txtCurrentUpdates.setText(current + "\n" + update
        + "\n#########################################");
    // Keep the scrollbar at the bottom
    txtCurrentUpdates.setCaretPosition(txtCurrentUpdates.getDocument().getLength());
  }
}