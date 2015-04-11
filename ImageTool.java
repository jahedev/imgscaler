package imgscaler;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageTool
{
	static BufferedImage image;
	
	// array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
        "jpg", "JPG", "gif", "GIF", "png", "PNG", "bmp", "BMP", // and other formats you need
    };
	
	static final FilenameFilter DIRECTORY_FILTER = new FilenameFilter()
	{

		@Override
		public boolean accept(File current, String name)
		{
			return new File(current, name).isDirectory();
		}
	};
	
	
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

	/* NOT USED FOR NOW
	public static void updateAllDirectoryContents(File dir)
	{
		try
		{
			File[] files = dir.listFiles();
			for (File file : files)
			{
				if (file.isDirectory())
				{
					System.out.println("Processing Directory \""
							+ file.getName() + "\"" + file.getCanonicalPath());
					updateAllDirectoryContents(file);
				} else
				{
					System.out.println("\tFile: " + file.getCanonicalPath());

					image = ImageIO.read(new File(file.getAbsolutePath()
							.toString()));
					createSmallerImage(image,
							file.getAbsolutePath().replace(Pref.source, "")
									.replace(file.getName(), ""),
							file.getName());
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	*/

	public static void updateChangesInDirectory(File dir)
	{
		try
		{
			File[] files = dir.listFiles(/* IMAGE_FILTER */);
			for (File file : files)
			{
				if (file.isDirectory())
				{
					System.out.println("Processing Directory: "
							+ file.getCanonicalPath());
					updateChangesInDirectory(file);
				} else
				{
					// String orginalImage = Pref.source +
					// file.getAbsolutePath().replace(Pref.source,
					// "").replace(file.getName(), "") + file.getName();
					String saveImage = Pref.dest
							+ file.getAbsolutePath().replace(Pref.source, "")
									.replace(file.getName(), "")
							+ file.getName();

					if (!(new File(saveImage).exists()))
					{
						System.out
								.println("\tFile: " + file.getCanonicalPath());
						// System.out.println(Pref.dest +
						// file.getAbsolutePath().replace(Pref.source,
						// "").replace(file.getName(), ""));
						// new File(Pref.dest +
						// file.getAbsolutePath().replace(Pref.source,
						// "").replace(file.getName(), "")).mkdirs();
						
						image = ImageIO.read(new File(file.getAbsolutePath()
								.toString()));
						
						// File sourceImageFile = file.getAbsoluteFile();
						File destImageFile =  new File(Pref.dest + file.getAbsolutePath().replace(Pref.source, "").replace(file.getName() + file.getName(), ""));
						File watermarkImageFile = new File((Main.class.getClass().getResource("/resources/logo.png").getPath()));
						
						createSmallerImage(image,
								file.getAbsolutePath().replace(Pref.source, "")
										.replace(file.getName(), ""),
								file.getName());
						

						addWatermark(destImageFile, destImageFile, watermarkImageFile);
					}
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void deleteChangesInDirectory(File dir)
	{
		try
		{
			File[] files = dir.listFiles(/* IMAGE_FILTER */);
			for (File file : files)
			{
				if (file.isDirectory())
				{
					System.out.println("Processing Directory: "
							+ file.getCanonicalPath());
					deleteChangesInDirectory(file);
				} else
				{
					// String orginalImage = Pref.source +
					// file.getAbsolutePath().replace(Pref.source,
					// "").replace(file.getName(), "") + file.getName();
					String originalImage = Pref.source
							+ file.getAbsolutePath().replace(Pref.dest, "");

					String destinationImage = Pref.dest
							+ file.getAbsolutePath()
									.replace(
											new File(Pref.dest)
													.getAbsolutePath(),
											"").replace(file.getName(), "")
							+ file.getName();

					File originalImageFile = new File(originalImage);
					File destinationImageFile = new File(destinationImage);

					if (!originalImageFile.exists())
					{
						System.out.println("\tDelete File: "
								+ originalImageFile.getAbsolutePath());
						destinationImageFile.delete();
					}
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/* OLD
	public static void createSmallerImage(BufferedImage img, String path,
			String name)
	{
		// Create quickly, then smooth and brighten it.
		img = Scalr.resize(img, Method.ULTRA_QUALITY, Pref.resizeProportion, Scalr.OP_ANTIALIAS);

		try
		{
			new File(Pref.dest + path).mkdirs();

			// Save Image File
			File outputfile = new File(Pref.dest + path + name);

			ImageIO.write(img, "jpg", outputfile);
		} catch (IOException e)
		{

		}
	}
	*/
	
	public static void createSmallerImage(BufferedImage img, String path, String name)
	{
		try
		{
			int width = (int) (Pref.resizeProportion * img.getWidth());
			int height = (int) (Pref.resizeProportion * img.getHeight());
			
			BufferedImage scaled = ImageScaler.getScaledInstance(img, width, height,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
			
			// Create path directory if it doesn't exist
			new File(Pref.dest + path).mkdirs();
			
			// Save Image File
						File outputfile = new File(Pref.dest + path + name);
			
			ImageScaler.writeJPG(scaled, new FileOutputStream(outputfile), (float) Pref.imageResizeQuality);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addWatermark(File sourceImageFile, File destImageFile,
			File watermarkImageFile)
	{
		try
		{
			BufferedImage sourceImage = ImageIO.read(sourceImageFile);

			BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

			// initializes necessary graphic properties

			Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();

			AlphaComposite alphaChannel = AlphaComposite.getInstance(

			AlphaComposite.SRC_OVER, (float) Pref.watermarkTransparency);

			g2d.setComposite(alphaChannel);

			// calculates the coordinate where the image is painted

			int topLeftX = (sourceImage.getWidth() - watermarkImage.getWidth());

			int topLeftY = (sourceImage.getHeight() - watermarkImage

			.getHeight());

			// paints the image watermark

			g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);

			ImageIO.write(sourceImage, "png", destImageFile);

			g2d.dispose();

		}
		catch (IOException ex)
		{

			System.err.println(ex);

		}
	}
}
