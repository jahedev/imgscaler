package imgscaler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main
{
	public static void main(String args[])
	{
		File settingsFile = new File(System.getProperty("user.home")
				+ File.separator + ".imgscaler");

		if (settingsFile.exists())
		{
			try (BufferedReader br = new BufferedReader(new FileReader(
					settingsFile)))
			{
				int lineNumber = 0;
				String line;
				
				String comment = ";";

				System.out.println("Settings: \n");

				while ((line = br.readLine()) != null)
				{
					// Skips empty lines and lines that begin with "//"
					if (line.startsWith(comment) || line.equals(""))
						continue;
					else
						lineNumber++;
					
					// Ignores any part of a valid line from where ";" is. i.e. " D:\\stuff\\ ; Pictures Folder "
					if (line.indexOf(comment) != -1)
						line = line.substring(0, line.indexOf(comment));
					
					line = line.trim();
					
					switch (lineNumber)
					{
					case 1:
						Pref.resizeProportion = Double.parseDouble(line);
						
						break;
					case 2:
						Pref.imageResizeQuality = Double.parseDouble(line);
						
						break;
					case 3:
						Pref.watermarkTransparency = Double.parseDouble(line);
						
						break;					case 4:
						Pref.source = line;
				
						break;
					case 5:
						Pref.dest = line;
				
						break;
					case 6:
						Pref.picturesFolder = line;
					
						break;
					case 7:
						Pref.destFolder = line;
				
						break;
					}
				}
				
				System.out.println("Resize Proportion: "
						+ Pref.resizeProportion);
				System.out.println("Image Resize Quality: " + (Pref.resizeProportion * 100.0) + "%");
				System.out.println("Source Directory: " + Pref.source);
				System.out.println("Destination Directory: "
						+ Pref.dest);
				System.out.println("Pictures Folder: "
						+ Pref.picturesFolder);
				System.out.println("Destination Folder: "
						+ Pref.destFolder);
				System.out.println("User Home Path: " + System.getProperty("user.home"));
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		File picDir = new File(Pref.source + Pref.picturesFolder);
		File destDir = new File(Pref.dest + Pref.destFolder);

		// updateAllDirectoryContents(picDir);

		// if (countFilesInDirectory(picDir) != countFilesInDirectory(destDir))
		// {
		System.out.println();
		System.out.println("Updating any CHANGES to Destination:");
		System.out.println();

		ImageTool.updateChangesInDirectory(picDir);

		System.out.println();
		System.out.println("Delete any NEW Images from Destination:");
		System.out.println();

		ImageTool.deleteChangesInDirectory(destDir);

		System.out.println();
		System.out.println("Done.");
	}
}
