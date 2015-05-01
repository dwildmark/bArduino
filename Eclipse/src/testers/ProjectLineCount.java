package testers;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class ProjectLineCount {
	private static int linesOfCode = 0;
	private static int filesInProject = 0;
	public static void main(String[] args) throws Exception {
		
		Files.walk(Paths.get("./src")).forEach(filePath -> {
		    if (Files.isRegularFile(filePath)) {
		    	filesInProject++;
		    	try {
					LineNumberReader  lnr = new LineNumberReader(new FileReader(filePath.toFile()));
					lnr.skip(Long.MAX_VALUE);
					linesOfCode += lnr.getLineNumber() + 1;
					lnr.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
		        System.out.println(filePath);
		    }
		});
		
		JOptionPane.showMessageDialog(null, linesOfCode + " lines of code in project\n" + filesInProject + " files");
	}

}
