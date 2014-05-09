package assembly;

public class Assembler {
	String[] filteredInstructions, filteredData;
	LabelManager labelManager;
	public Assembler(String[] instructions, String[] data) throws DuplicateLabelException {
		labelManager = new LabelManager();
		
		filteredData = data;
		for(int i = 0; i < data.length; i++) {
			int idx = data[i].indexOf(':'), idx2 = data[i].indexOf('"');
			if(idx2 != -1 && idx > idx2) {
				continue;
			} else {
				String label = data[i].substring(0, idx);
				if(labelManager.containsLabel(label))
					throw new DuplicateLabelException();
				labelManager.setLabel(label, i, 0);
			}
		}
		
		filteredInstructions = new String[instructions.length];
		for(int i = 0; i < instructions.length; i++) {
			int idx = instructions[i].indexOf(':');
			if(idx == -1) {
				filteredInstructions[i] = instructions[i];
			} else {
				String label = instructions[i].substring(0, idx);
				filteredInstructions[i] = instructions[i].substring(idx+1);
				if(labelManager.containsLabel(label))
					throw new DuplicateLabelException();
				labelManager.setLabel(label, i, 1);
			}
		}
	}
	
	public String[] getFilteredInstructions() {
		return filteredInstructions;
	}
	
	public String[] getFilteredData() {
		return filteredData;
	}
	
	public LabelManager getLabeManager() {
		return labelManager;
	}
}
