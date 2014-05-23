package ui;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import registers.Register;
import registers.RegisterManager;
import simulation.Simulator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JTextField;

public class GUI {
	
	private Simulator simulator;

	private JFrame frmOraka;
	private JTable registerTable;
	private JTable table;
	private JTable memoryTable;
	private JTextPane logTextPane;
	private JTable reigsterTable;
	private JTextArea editor;
	private String currentFilePath = "";

	private JLabel lblExMem;
	private JLabel lblIdex;
	private JLabel lblIfid;
	private JLabel lblMemwb;
	private JLabel exmemVal;
	private JLabel idexVal;
	private JLabel ifidVal;
	private JLabel memwbVal;

	// private File file;

	private RegisterManager rm = new RegisterManager();

	private String RegDst = "0";
	private String Jump = "0";
	private String Branch = "0";
	private String MemRead = "0";
	private String MemToReg = "0";
	private String ALUOp = "00";
	private String MemWrite = "0";
	private String ALUSrc = "0";
	private String RegWrite = "0";
	private String Zero = "0";

	private JTable controlTable;

	private String programStartAddress = "400";
	private JTextField programStartAddressTextField;
	private JTable dataMemoryTable;
	private JTable programMemoryTable;
	private JTextField dataMemoryAddressTextField;
	private JTextField dataMemoryValueTextField;
	private JTextField programMemoryAddressTextField;
	private JTextField programMemoryValueTextField;

	// private String filePath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmOraka.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmOraka = new JFrame();
		frmOraka.setTitle("ORAKA MIPS Simulator");
		frmOraka.setBounds(100, 100, 1000, 550);
		// frame.setBounds(100, 100, 450, 300);
		frmOraka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmOraka.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("Assemble");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				assembleProgram();
			}
		});
		btnNewButton.setBounds(341, 6, 105, 29);
		frmOraka.getContentPane().add(btnNewButton);

		JButton btnCompile = new JButton("Run");
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runProgram();
			}
		});
		btnCompile.setBounds(439, 6, 65, 29);
		frmOraka.getContentPane().add(btnCompile);

		JButton btnOpenFile = new JButton("Open File");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		btnOpenFile.setBounds(119, 6, 117, 29);
		frmOraka.getContentPane().add(btnOpenFile);

		JButton btnSaveFile = new JButton("Save File");
		btnSaveFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentFilePath.equals(""))
					saveAs();
				else
					save();
			}
		});
		btnSaveFile.setBounds(230, 6, 117, 29);
		frmOraka.getContentPane().add(btnSaveFile);

		JButton btnStep = new JButton("Step");
		btnStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performStep();
			}
		});
		btnStep.setBounds(497, 6, 75, 29);
		frmOraka.getContentPane().add(btnStep);

		JButton btnStop = new JButton("Stop/Reset");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resetAll();
			}
		});
		btnStop.setBounds(568, 6, 105, 29);
		frmOraka.getContentPane().add(btnStop);

		JButton btnNewFile = new JButton("Clear/New File");
		btnNewFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFile();
			}
		});
		btnNewFile.setBounds(6, 6, 117, 29);
		frmOraka.getContentPane().add(btnNewFile);

		JScrollPane editorScrollPane = new JScrollPane();
		editorScrollPane.setBounds(16, 47, 430, 310);
		frmOraka.getContentPane().add(editorScrollPane);

		editor = new JTextArea();
		editorScrollPane.setViewportView(editor);

		// TODO Comment the next 2 lines if you want to use the WindowBuilder
		// TextLineNumber tln = new TextLineNumber(editor);
		// editorScrollPane.setRowHeaderView(tln);

		JScrollPane registerTableScrollPane = new JScrollPane();
		registerTableScrollPane.setBounds(453, 47, 197, 310);
		frmOraka.getContentPane().add(registerTableScrollPane);

		Object registerTableColumnNames[] = { "Title", "#", "Value" };
		Object[][] registerData = getRegisterValues();

		registerTable = new JTable(registerData, registerTableColumnNames);
		registerTableScrollPane.setViewportView(registerTable);
		registerTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		registerTable.getColumnModel().getColumn(1).setPreferredWidth(75);
		registerTable.getColumnModel().getColumn(2).setPreferredWidth(200);

		Object rowData[][] = { { "$s1", "32", "0x2222" },
				{ "$v1", "1", "0x1234" } };
		Object columnNames[] = { "Title", "#", "Value" };

		JScrollPane logScrollPane = new JScrollPane();
		logScrollPane.setBounds(16, 389, 440, 115);
		frmOraka.getContentPane().add(logScrollPane);

		logTextPane = new JTextPane();
		logScrollPane.setColumnHeaderView(logTextPane);
		logTextPane.setEditable(false);

		lblExMem = new JLabel("EX/MEM");
		lblExMem.setBounds(468, 389, 61, 16);
		frmOraka.getContentPane().add(lblExMem);

		lblIdex = new JLabel("ID/EX");
		lblIdex.setBounds(468, 417, 61, 16);
		frmOraka.getContentPane().add(lblIdex);

		lblIfid = new JLabel("IF/ID");
		lblIfid.setBounds(468, 445, 61, 16);
		frmOraka.getContentPane().add(lblIfid);

		lblMemwb = new JLabel("MEM/WB");
		lblMemwb.setBounds(468, 473, 61, 16);
		frmOraka.getContentPane().add(lblMemwb);

		exmemVal = new JLabel("-----");
		exmemVal.setBounds(541, 389, 312, 16);
		frmOraka.getContentPane().add(exmemVal);

		idexVal = new JLabel("-----");
		idexVal.setBounds(541, 417, 312, 16);
		frmOraka.getContentPane().add(idexVal);

		ifidVal = new JLabel("-----");
		ifidVal.setBounds(541, 445, 312, 16);
		frmOraka.getContentPane().add(ifidVal);

		memwbVal = new JLabel("-----");
		memwbVal.setBounds(541, 473, 312, 16);
		frmOraka.getContentPane().add(memwbVal);

		JScrollPane controlScrollPane = new JScrollPane();
		controlScrollPane.setBounds(865, 47, 129, 203);
		frmOraka.getContentPane().add(controlScrollPane);

		Object controlTableColumns[] = { "Title", "Value" };
		Object[][] controlData = getControlValues();

		controlTable = new JTable(controlData, controlTableColumns);
		controlScrollPane.setViewportView(controlTable);

		JLabel lblProgramStartAddress = new JLabel("Program Start Adr.");
		lblProgramStartAddress.setBounds(865, 262, 129, 16);
		frmOraka.getContentPane().add(lblProgramStartAddress);

		programStartAddressTextField = new JTextField();
		programStartAddressTextField.setBounds(860, 283, 134, 28);
		frmOraka.getContentPane().add(programStartAddressTextField);
		programStartAddressTextField.setColumns(10);

		JButton btnSetAddress = new JButton("Set Address");
		btnSetAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String addr = programStartAddressTextField.getText().trim();
				if (!isNumeric(addr)) {
					log("Please enter a valid program start address and make sure it is greater than 400");
				} else {
					int addrVal = Integer.parseInt(addr);
					if (addrVal < 400)
						log("Please enter a value greater than 400");
					else {
						programStartAddress = (new Integer(addrVal)).toString();
						log("Program Start Address has been set to "
								+ programStartAddress);
					}
				}
			}
		});
		btnSetAddress.setBounds(865, 328, 117, 29);
		frmOraka.getContentPane().add(btnSetAddress);
		
		JScrollPane dataMemoryScrollPane = new JScrollPane();
		dataMemoryScrollPane.setBounds(662, 47, 197, 115);
		frmOraka.getContentPane().add(dataMemoryScrollPane);
		
		Object dataMemoryTableCols[] = { "Address", "Value" };
		Object[][] dataMemoryData = getDataMemoryValues();

		
		dataMemoryTable = new JTable(dataMemoryData, dataMemoryTableCols);
		dataMemoryScrollPane.setViewportView(dataMemoryTable);
		
		JScrollPane programMemoryScrollPane = new JScrollPane();
		programMemoryScrollPane.setBounds(662, 224, 197, 90);
		frmOraka.getContentPane().add(programMemoryScrollPane);
		
		Object[] programMemoryCols = {"Address", "Value" };
		Object[][] programMemoryData = getProgramMemoryData();
		
		programMemoryTable = new JTable(programMemoryData, programMemoryCols);
		programMemoryScrollPane.setViewportView(programMemoryTable);
		
		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(662, 166, 61, 16);
		frmOraka.getContentPane().add(lblAddress);
		
		JLabel lblValue = new JLabel("Value");
		lblValue.setBounds(735, 166, 61, 16);
		frmOraka.getContentPane().add(lblValue);
		
		JLabel lblAddress_1 = new JLabel("Address");
		lblAddress_1.setBounds(662, 318, 61, 16);
		frmOraka.getContentPane().add(lblAddress_1);
		
		JLabel lblValue_1 = new JLabel("Value");
		lblValue_1.setBounds(735, 318, 61, 16);
		frmOraka.getContentPane().add(lblValue_1);
		
		dataMemoryAddressTextField = new JTextField();
		dataMemoryAddressTextField.setBounds(662, 184, 65, 28);
		frmOraka.getContentPane().add(dataMemoryAddressTextField);
		dataMemoryAddressTextField.setColumns(10);
		
		dataMemoryValueTextField = new JTextField();
		dataMemoryValueTextField.setBounds(730, 184, 65, 28);
		frmOraka.getContentPane().add(dataMemoryValueTextField);
		dataMemoryValueTextField.setColumns(10);
		
		JButton btnSetDataMemory = new JButton("SDM");
		btnSetDataMemory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				setDataMemory()
			}
		});
		btnSetDataMemory.setBounds(792, 183, 65, 29);
		frmOraka.getContentPane().add(btnSetDataMemory);
		
		programMemoryAddressTextField = new JTextField();
		programMemoryAddressTextField.setBounds(662, 346, 61, 28);
		frmOraka.getContentPane().add(programMemoryAddressTextField);
		programMemoryAddressTextField.setColumns(10);
		
		programMemoryValueTextField = new JTextField();
		programMemoryValueTextField.setBounds(735, 346, 61, 28);
		frmOraka.getContentPane().add(programMemoryValueTextField);
		programMemoryValueTextField.setColumns(10);
		
		JButton btnSpm = new JButton("SPM");
		btnSpm.setBounds(792, 347, 61, 29);
		frmOraka.getContentPane().add(btnSpm);

		setEXMEM("/\\/\\/\\/\\/\\");
		setIDEX("/\\/\\/\\/\\/\\");
		setIFID("/\\/\\/\\/\\/\\");
		setMEMWB("/\\/\\/\\/\\/\\");

		log("-- Simulator just started --");
		// logTextPane.in

	}

	public void log(String s) {
		logTextPane.setText(logTextPane.getText() + s + "\n");
	}

	public boolean setRegisterValue() {
		// TODO
		return true;
	}
	
	public Object[][] getProgramMemoryData() {
		Object[][] values = null;
		Hashtable<Long, String> programContents = simulator.getInstructionMemoryContents();
		
		values = new Object[programContents.size()][2];

		int count = 0;
		for(Entry<Long, String> entry: programContents.entrySet()) {
			values[count][0] = entry.getKey();
			values[count][1] = entry.getValue();
			count++;
		}
		return values;
	}
	
	public Object[][] getDataMemoryValues() {
		Object[][] values = null;
		Hashtable<Long, String> memoryContents = simulator.getDataMemoryContents();
		
		values = new Object[memoryContents.size()][2];

		int count = 0;
		for(Entry<Long, String> entry: memoryContents.entrySet()) {
			values[count][0] = entry.getKey();
			values[count][1] = entry.getValue();
			count++;
		}
		return values;
	}

	private Object[][] getRegisterValues() {
		Object[][] values = new Object[32][3];
		for (int i = 0; i < 32; i++) {
			Register r = rm.getRegister(i);
			String regTitle = r.getTitle();
			Integer regNumber = new Integer(i);
			String regValue = rm.formatHex(rm.binaryToHex(r.getValue()));
			values[i][0] = regTitle;
			values[i][1] = regNumber;
			values[i][2] = regValue;
		}
		return values;
	}
	
	private void setPipelineRegisterValues() {
		Hashtable<String, String> registers = simulator.getPipelineRegistersContents();
		
		for(Entry<String, String> entry: registers.entrySet()) {
			if(entry.getKey().equals("EX/MEM") || entry.getKey().equals("EXMEM"))
				setEXMEM(entry.getValue());
			else if (entry.getKey().equals("ID/EX") || entry.getKey().equals("IDEX"))
				setIDEX(entry.getValue());
			else if (entry.getKey().equals("IF/ID") || entry.getKey().equals("IFID"))
				setIFID(entry.getValue());
			else
				setMEMWB(entry.getValue());
		}
	}

	private Object[][] getControlValues() {
		Object[][] values = new Object[10][2];
		values[0][0] = "RegDst";
		values[1][0] = "Jump";
		values[2][0] = "Branch";
		values[3][0] = "MemRead";
		values[4][0] = "MemToReg";
		values[5][0] = "ALUOp";
		values[6][0] = "MemWrite";
		values[7][0] = "ALUSrc";
		values[8][0] = "RegWrite";
		values[9][0] = "Zero";

		values[0][1] = "0";
		values[1][1] = "0";
		values[2][1] = "0";
		values[3][1] = "0";
		values[4][1] = "0";
		values[5][1] = "00";
		values[6][1] = "0";
		values[7][1] = "0";
		values[8][1] = "0";
		values[9][1] = "0";

		return values;
	}

	public void runProgram() {
		if (currentFilePath.equals("")) {
			log("There seems to be no file currently open");
			log("Make sure you have saved the currently open file before running");

			return;
		}

		// TODO ELSE perform run.
	}

	public void assembleProgram() {
		log("Assembling program upto latest save performed");
	}

	public void performStep() {
		// TODO
	}

	public void resetAll() {

	}

	private void clearFile() {
		currentFilePath = "";
		editor.setText("");
		log("Cleared editor and file path.");
	}

	private void save() {
		File f = new File(currentFilePath);

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(f));

			editor.write(out);
			log("File changes succesfully saved [" + currentFilePath + "]");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log("Error occured while trying to save file.");
			e.printStackTrace();
		}

	}

	private void saveAs() {
		FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter(
				"ASM", "mips");
		final JFileChooser saveAsFileChooser = new JFileChooser();
		saveAsFileChooser.setApproveButtonText("Save");
		saveAsFileChooser.setFileFilter(extensionFilter);
		int actionDialog = saveAsFileChooser.showSaveDialog(frmOraka);
		if (actionDialog != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = saveAsFileChooser.getSelectedFile();
		if (!file.getName().endsWith(".asm")) {
			file = new File(file.getAbsolutePath() + ".asm");
			currentFilePath = file.getAbsolutePath();
			System.out.println(currentFilePath);
		}

		BufferedWriter outFile = null;
		try {
			outFile = new BufferedWriter(new FileWriter(file));

			editor.write(outFile);
			log("File succesfully saved [" + currentFilePath + "]");
		} catch (IOException ex) {
			ex.printStackTrace();
			log("Error occured while trying to save file.");
		} finally {
			if (outFile != null) {
				try {
					outFile.close();
				} catch (IOException e) {
					log("Error occured while trying to save file.");
				}
			}
		}
	}

	private void openFile() {
		JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(frmOraka);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			try {
				// What to do with the file, e.g. display it in a TextArea
				editor.read(new FileReader(file.getAbsolutePath()), null);
				currentFilePath = file.getAbsolutePath();
				log("Opened [" + currentFilePath + "]");
			} catch (IOException ex) {
				log("An error occured trying to open [" + currentFilePath + "]");
			}
		} else {
			log("File access not permitted by system");
		}
	}

	public void setEXMEM(String val) {
		exmemVal.setText(val);
	}

	public void setIDEX(String val) {
		idexVal.setText(val);
	}

	public void setIFID(String val) {
		ifidVal.setText(val);
	}

	public void setMEMWB(String val) {
		memwbVal.setText(val);
	}

	public void setRegDst(String val) {
		RegDst = val;
		controlTable.setValueAt(new String(val), 0, 1);
	}

	public void setJump(String val) {
		Jump = val;
		controlTable.setValueAt(new String(val), 1, 1);
	}

	public void setBranch(String val) {
		Branch = val;
		controlTable.setValueAt(new String(val), 2, 1);
	}

	public void setMemRead(String val) {
		MemRead = val;
		controlTable.setValueAt(new String(val), 3, 1);
	}

	public void setMemtoReg(String val) {
		MemToReg = val;
		controlTable.setValueAt(new String(val), 4, 1);
	}

	public void setALUOp(String val) {
		ALUOp = val;
		controlTable.setValueAt(new String(val), 5, 1);
	}

	public void setMemWrite(String val) {
		MemWrite = val;
		controlTable.setValueAt(new String(val), 6, 1);
	}

	public void setALUSrc(String val) {
		ALUSrc = val;
		controlTable.setValueAt(new String(val), 7, 1);
	}

	public void setRegWrite(String val) {
		RegWrite = val;
		controlTable.setValueAt(new String(val), 8, 1);
	}

	public void setZero(String val) {
		Zero = val;
		controlTable.setValueAt(new String(val), 9, 1);
	}

	private boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	public String getProgramStartAddress() {
		return programStartAddress;
	}
}