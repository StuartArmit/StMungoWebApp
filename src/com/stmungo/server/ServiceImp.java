package com.stmungo.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.stmungo.client.model.TranslatorChoice;
import com.stmungo.client.model.TranslatorMain;
import com.stmungo.client.model.TranslatorProtocol;
import com.stmungo.client.model.TranslatorRole;
import com.stmungo.client.model.TranslatorTypestate;
import com.stmungo.client.model.ValidatorChoice;
import com.stmungo.client.model.ValidatorMain;
import com.stmungo.client.model.ValidatorProtocol;
import com.stmungo.client.model.ValidatorRole;
import com.stmungo.client.model.ValidatorTypestate;
import com.stmungo.client.service.Service;

//Service implementation requires a method for each service stream creating an instance 
//of each model object to enable synchronicity of processes each method receives string 
//data from server side and returns object string data as required. Methods utilise jar
//files passing required it's required input, extracting the output as required i.e. via
//file generation or console capture. 

public class ServiceImp extends RemoteServiceServlet implements Service {
	String localName;
//	String globDir =  System.getProperty( "catalina.base" ) + "/webapps";			//to run on server or tomcat localhost 
//	String globStJar = globDir + "/StMungoApp/WEB-INF/lib/stmungo.jar";
//	String globMJar = globDir + "/StMungoApp/WEB-INF/lib/mungo.jar";
	String globDir = System.getProperty("user.dir").toString().replace('\\', '/'); //to run locally from file repository 
	String globStJar = globDir + "/WEB-INF/lib/stmungo.jar";
	String globMJar = globDir + "/WEB-INF/lib/mungo.jar";

	@Override
	public TranslatorMain getTranslatorMain(String text) {
		TranslatorMain mainText = new TranslatorMain();
		FileWriter inputFile;
		try {
			inputFile = new FileWriter(globDir + "/input.scr");
			inputFile.write(text);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globStJar, globDir+"/input.scr");
			pb.directory(new File(globDir + "/store"));
			Process p = pb.start();

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			FileNameFinder fnf = new FileNameFinder();
			localName = (fnf.nameFind(text));
			String fileName = localName;
			if (fileName.contentEquals("ERROR")) {
				mainText.setText("An error has occured when proccessing, please check your data and try again");
			}

			String nameMain = fileName + "Main.java";
			String dir = globDir;

			FileFinder ff = new FileFinder();
			String loc = (ff.dirFind(nameMain, new File(dir)));
			BufferedReader reader = new BufferedReader(new FileReader(loc + "/" + nameMain));

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String content = stringBuilder.toString();
			mainText.setText(content);

			return mainText;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return mainText;
	}

	@Override
	public ValidatorMain getValidatorMain(String vText) {
		ValidatorMain isValid = new ValidatorMain();
		if (localName == null || vText.equals("") || vText.equals(" ")|| vText.equals("An error has occured when proccessing, please check your data and try again")) {
			isValid.setText("Validation Failed");
			return isValid;
		}

		String fileName = localName;
		String nameMain = fileName + "Main.java";
		String dir = globDir;

		FileFinder ff = new FileFinder();
		String loc = (ff.dirFind(nameMain, new File(dir)));

		FileWriter inputFile;
		try {
			inputFile = new FileWriter(loc + "/" + nameMain);
			inputFile.write(vText);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Process p = Runtime.getRuntime().exec("java -jar " + globMJar + globDir + loc + nameMain);
			p.waitFor();

			InputStream is = p.getInputStream();

			byte b[] = new byte[is.available()];
			is.read(b, 0, b.length);
			String s = new String(b);

			isValid.setText(s);
			if (s.equals("")) {
				s = "Validation Successful";
				isValid.setText(s);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			isValid.setText("Validation Failed");
			return isValid;
		}
		return isValid;
	}

	@Override
	public TranslatorRole getTranslatorRole(String text) {
		TranslatorRole roleText = new TranslatorRole();
		try {

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globStJar, globDir+"/input.scr");
			pb.directory(new File(globDir + "/store"));			
			Process p = pb.start();

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			FileNameFinder fnf = new FileNameFinder();
			String fileName = (fnf.nameFind(text));
			if (fileName.contentEquals("ERROR")) {
				roleText.setText(" ");
			}

			String nameRole = fileName + "Role.java";
			String dir = globDir;

			FileFinder ff = new FileFinder();
			String loc = (ff.dirFind(nameRole, new File(dir)));

			BufferedReader reader;
			reader = new BufferedReader(new FileReader(loc + "/" + nameRole));

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String content = stringBuilder.toString();
			roleText.setText(content);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return roleText;
	}

	@Override
	public ValidatorRole getValidatorRole(String vText) {
		ValidatorRole isValid = new ValidatorRole();

		if (localName == null || vText.equals("") || vText.equals(" ")) {
			isValid.setText("Validation Failed");
			return isValid;
		}

		String fileName = localName;
		String nameMain = fileName + "Role.java";
		String dir = globDir;

		FileFinder ff = new FileFinder();
		String loc = (ff.dirFind(nameMain, new File(dir)));

		FileWriter inputFile;
		try {
			inputFile = new FileWriter(loc + "/" + nameMain);
			inputFile.write(vText);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Process p = Runtime.getRuntime().exec("java -jar " + globMJar + globDir + "/" + loc + nameMain);
			p.waitFor();

			InputStream is = p.getInputStream();

			byte b[] = new byte[is.available()];
			is.read(b, 0, b.length);
			String s = new String(b);

			isValid.setText(s);
			if (s.equals("")) {
				s = "Validation Successful";
				isValid.setText(s);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			isValid.setText("Validation Failed");
			return isValid;
		}
		return isValid;
	}

	@Override
	public TranslatorProtocol getTranslatorProtocol(String text) {
		TranslatorProtocol protocolText = new TranslatorProtocol();
		try {

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globStJar, globDir+"/input.scr");
			pb.directory(new File(globDir + "/store"));			
			Process p = pb.start();

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			FileNameFinder fnf = new FileNameFinder();
			String fileName = (fnf.nameFind(text));
			if (fileName.contentEquals("ERROR")) {
				protocolText.setText("");
			}

			String nameRole = fileName + "Protocol.protocol";
			String dir = globDir;

			FileFinder ff = new FileFinder();
			String loc = (ff.dirFind(nameRole, new File(dir)));

			BufferedReader reader;
			reader = new BufferedReader(new FileReader(loc + "/" + nameRole));

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String content = stringBuilder.toString();
			protocolText.setText(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return protocolText;
	}

	@Override
	public ValidatorProtocol getValidatorProtocol(String vText) {
		ValidatorProtocol isValid = new ValidatorProtocol();

		if (localName == null || vText.equals("") || vText.equals(" ")) {
			isValid.setText("Validation Failed");
			return isValid;
		}

		String fileName = localName;
		String nameMain = fileName + "Protocol.java";
		String dir = globDir;

		FileFinder ff = new FileFinder();
		String loc = (ff.dirFind(nameMain, new File(dir)));

		FileWriter inputFile;
		try {
			inputFile = new FileWriter(loc + "/" + nameMain);
			inputFile.write(vText);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globMJar + loc + "/" + nameMain);

			Process p = pb.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			if (result.equals("")) {
				result = "Validation Successful";
			}
			isValid.setText(result);

		} catch (IOException e) {
			e.printStackTrace();
			isValid.setText("Validation Failed");
			return isValid;
		}
		return isValid;
	}

	@Override
	public TranslatorChoice getTranslatorChoice(String text) {
		TranslatorChoice choiceText = new TranslatorChoice();
		try {

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globStJar, globDir+"/input.scr");
			pb.directory(new File(globDir + "/store"));			
			Process p = pb.start();

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			FileNameFinder fnf = new FileNameFinder();
			String fileName = (fnf.nameFind(text));
			if (fileName.contentEquals("ERROR")) {
				choiceText.setText("");
			}

			String nameRole = fileName + "Choice1.java";
			String dir = globDir;

			FileFinder ff = new FileFinder();
			String loc = (ff.dirFind(nameRole, new File(dir)));

			BufferedReader reader;
			reader = new BufferedReader(new FileReader(loc + "/" + nameRole));

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String content = stringBuilder.toString();
			choiceText.setText(content);


		String nameChoiceTwo = localName + "Choice2.java";
		String nameChoiceThree = localName + "Choice3.java";

		File f = new File(loc, nameChoiceTwo);
		if (f.exists() == false) {
			return choiceText;
		}

		try {
			BufferedReader readerTwo;
			readerTwo = new BufferedReader(new FileReader(loc + "/" + nameChoiceTwo));

			StringBuilder stringBuilderTwo = new StringBuilder();
			String lineTwo = null;
			String lsTwo = System.getProperty("line.separator");

			while ((lineTwo = readerTwo.readLine()) != null) {
				stringBuilderTwo.append(lineTwo);
				stringBuilderTwo.append(lsTwo);
			}

			stringBuilderTwo.deleteCharAt(stringBuilderTwo.length() - 1);
			readerTwo.close();

			String contentTwo = stringBuilderTwo.toString();
			String cTwo = choiceText.getText();
			String concatanatedTwo = cTwo.concat(contentTwo);
			choiceText.setText(concatanatedTwo);

		} catch (IOException e) {
			e.printStackTrace();
		}
		File fi = new File(loc, nameChoiceThree);
		if (fi.exists() == false) {
			return choiceText;
		}

		BufferedReader readerThree;
		readerThree = new BufferedReader(new FileReader(loc + "/" + nameChoiceThree));

		StringBuilder stringBuilderThree = new StringBuilder();
		String lineThree = null;
		String lsThree = System.getProperty("line.separator");

		while ((lineThree = readerThree.readLine()) != null) {
			stringBuilderThree.append(lineThree);
			stringBuilderThree.append(lsThree);
		}

		stringBuilderThree.deleteCharAt(stringBuilderThree.length() - 1);
		readerThree.close();

		String contentThree = stringBuilderThree.toString();
		String cThree = choiceText.getText();
		String concatanatedThree = cThree.concat(contentThree);
		choiceText.setText(concatanatedThree);

	} catch (IOException e) {
		e.printStackTrace();
	}
	return choiceText;
}

	@Override
	public ValidatorChoice getValidatorChoice(String vText) {
		ValidatorChoice isValid = new ValidatorChoice();

		if (localName == null || vText.equals("") || vText.equals(" ")) {
			isValid.setText("Validation Failed");
			return isValid;
		}

		String fileName = localName;
		String nameMain = fileName + "Choice1.java";
		String dir = globDir;

		FileFinder ff = new FileFinder();
		String loc = (ff.dirFind(nameMain, new File(dir)));

		FileWriter inputFile;
		try {
			inputFile = new FileWriter(loc + "/" + nameMain);
			inputFile.write(vText);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globMJar + "/" + loc + "/" + nameMain);

			Process p = pb.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			if (result.equals("")) {
				result = "Validation Successful";
			}
			isValid.setText(result);

		} catch (IOException e) {
			e.printStackTrace();
			isValid.setText("Validation Failed");
			return isValid;
		}
		return isValid;
	}

	@Override
	public TranslatorTypestate getTranslatorTypestate(String text) {
		TranslatorTypestate typestateText = new TranslatorTypestate();
		try {

			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globStJar, globDir+"/input.scr");
			pb.directory(new File(globDir + "/store"));			
			Process p = pb.start();

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			FileNameFinder fnf = new FileNameFinder();
			localName = (fnf.nameFind(text));
			String fileName = localName;
			if (fileName.contentEquals("ERROR")) {
				typestateText.setText("");
			}else {
			
			String nameTypestate = "Typestate.java";
			String dir = globDir;

			FileFinder ff = new FileFinder();
			String loc = (ff.dirFind(nameTypestate, new File(dir)));

			BufferedReader reader;
			reader = new BufferedReader(new FileReader(loc + "/" + nameTypestate));

			StringBuilder stringBuilder = new StringBuilder();
			String line = null;
			String ls = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			reader.close();

			String content = stringBuilder.toString();
			typestateText.setText(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return typestateText;
	}

	@Override
	public ValidatorTypestate getValidatorTypestate(String vText) {
		ValidatorTypestate isValid = new ValidatorTypestate();

		if (localName == null || vText.equals("") || vText.equals(" ")) {
			isValid.setText("Validation Failed");
			return isValid;
		}

		String fileName = localName;
		String nameMain = fileName + "Typestate.java";
		String dir = globDir;

		FileFinder ff = new FileFinder();
		String loc = (ff.dirFind(nameMain, new File(dir)));

		FileWriter inputFile;
		try {
			inputFile = new FileWriter(loc + "/" + nameMain);
			inputFile.write(vText);
			inputFile.flush();
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", globMJar + " " + loc + "/" + nameMain);

			Process p = pb.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
			String result = builder.toString();
			if (result.equals("")) {
				result = "Validation Successful";
			}
			isValid.setText(result);

		} catch (IOException e) {
			e.printStackTrace();
			isValid.setText("Validation Failed");
			return isValid;
		}
		return isValid;
	}

}
