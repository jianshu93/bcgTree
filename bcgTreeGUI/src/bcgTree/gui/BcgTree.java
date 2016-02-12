package bcgTree.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BcgTree extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		BcgTree mainWindow = new BcgTree();
		mainWindow.pack();
		mainWindow.setVisible(true);
	}

	private TextArea logTextArea;
	private GridLayout proteomesPanelLayout;
	private JPanel proteomesPanel;
	private Map<String, File> proteomes;
	
	public BcgTree(){
		proteomes = new HashMap<String, File>();
		initGUI();
	}
	
	public void initGUI(){
		// Basic settings
		this.setTitle("bcgTree");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());
		// Add title
		JLabel titleLabel = new JLabel("bcgTree v1.0.0");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		this.add(titleLabel, BorderLayout.NORTH);
		// Add "Run" button
		JButton runButton = new JButton("Run");
		this.add(runButton, BorderLayout.SOUTH);
		runButton.addActionListener(runActionListener);
		// Add central panel (split in parameter section and log/output section)
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1, 2));
		this.add(mainPanel, BorderLayout.CENTER);
		JPanel settingsPanel = new JPanel();
		mainPanel.add(settingsPanel);
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BorderLayout());
		mainPanel.add(logPanel);
		// Add Elements to settingsPanel
		JLabel proteomesLabel = new JLabel("Proteomes");
		settingsPanel.add(proteomesLabel);
		JButton proteomesAddButton = new JButton("+");
		proteomesAddButton.addActionListener(proteomeAddActionListener);
		settingsPanel.add(proteomesAddButton);
		proteomesPanel = new JPanel();
		proteomesPanelLayout = new GridLayout(0, 3);
		proteomesPanel.setLayout(proteomesPanelLayout);
		settingsPanel.add(proteomesPanel);
		// Add log textarea
		logTextArea = new TextArea();
		logTextArea.setEditable(false);
		logPanel.add(logTextArea, BorderLayout.CENTER);
		// final adjustments
		this.pack();
	}
	
	ActionListener runActionListener = new ActionListener() {		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Process proc = Runtime.getRuntime().exec("perl "+System.getProperty("user.dir")+"/../bin/bcgTree.pl --help");
				InputStream stdout = proc.getInputStream();
	            InputStreamReader isr = new InputStreamReader(stdout);
	            BufferedReader br = new BufferedReader(isr);
	            String line = null;
	            while ( (line = br.readLine()) != null)
	                logTextArea.append(line + "\n");
	            int exitVal = proc.waitFor();
	            System.out.println("Process exitValue: " + exitVal);
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	};
	
	ActionListener proteomeAddActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			proteomeAddAction();
		}
	};
	
	public void proteomeAddAction(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.showOpenDialog(this);
		File[] files = chooser.getSelectedFiles();
		proteomesPanelLayout.setRows(files.length);
		for(int i=0; i<files.length; i++){
			String name = files[i].getName();
			String path = files[i].getAbsolutePath();
			// avoid name collisions (does not matter if name and path are identical)
			if(proteomes.get(name) != null && !proteomes.get(name).getAbsolutePath().equals(path)){
				int suffix = 1;
				while(proteomes.get(name+"_"+suffix) != null && !proteomes.get(name+"_"+suffix).getAbsolutePath().equals(path)){
					suffix++;
				}
				name = name + "_" + suffix;
			}
			proteomes.put(name, files[i]);
		}
		updateProteomePanel();
	}
	
	public void updateProteomePanel(){
		proteomesPanel.removeAll();
		proteomesPanelLayout.setRows(proteomes.size());
		for(Map.Entry<String, File> entry : proteomes.entrySet()){
			JButton removeButton = new JButton("-");
			proteomesPanel.add(removeButton);
			JTextField proteomeNameTextField = new JTextField(entry.getKey());
			proteomesPanel.add(proteomeNameTextField);
			JLabel proteomePathLabel = new JLabel(entry.getValue().getAbsolutePath());
			proteomesPanel.add(proteomePathLabel);
		}
		this.revalidate();
		this.repaint();
	}

}
