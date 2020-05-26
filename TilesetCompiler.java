//Tileset Compiler

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class TilesetCompiler {
	private static int inputLength;
	
	public static void main(String[] args) {
		System.out.println("* Tileset Compiler *");
		if (args.length != 0 && args.length != 3) {
			error("This program's usage is as follows:\n" +
					"java TilesetCompiler\n" +
					"java TilesetCompiler <input directory location> <input resolution option> " +
					"<output tileset location>");
		}
		Scanner input = new Scanner(System.in);
		String inputDirectoryLocation;
		if (args.length == 0) {
			System.out.print("Enter the input directory's location: ");
			inputDirectoryLocation = input.nextLine();
		} else {
			inputDirectoryLocation = args[0];
		}
		File inputDirectory = new File(inputDirectoryLocation);
		if (!inputDirectory.isDirectory()) {
			error(inputDirectory + " is not a valid directory.");
		}
		String inputResolutionString;
		if (args.length == 0) {
			System.out.println("Select the input resolution for the images:");
			System.out.println("1) 8x8 pixels");
			System.out.println("2) 16x16 pixels");
			System.out.println("3) 32x32 pixels");
			System.out.println("4) 64x64 pixels");
			System.out.print("Input resolution option: ");
			inputResolutionString = input.nextLine();
		} else {
			inputResolutionString = args[1];
		}
		Scanner lineInput = new Scanner(inputResolutionString);
		if (!lineInput.hasNextInt()) {
			error("Invalid input resolution input.");
		}
		int inputResolutionOption = lineInput.nextInt();
		if (inputResolutionOption < 1 || inputResolutionOption > 4) {
			error("Invalid input resolution option.");
		}
		lineInput.close();
		inputLength = (int)Math.pow(2, inputResolutionOption + 2);
		String outputTilesetLocation;
		if (args.length == 0) {
			System.out.print("Enter the output tileset's location: ");
			outputTilesetLocation = input.nextLine();
		} else {
			outputTilesetLocation = args[2];
		}
		File outputTilesetDirectory = new File(outputTilesetLocation).getParentFile();
		if (outputTilesetDirectory != null && !outputTilesetDirectory.exists() && outputTilesetDirectory.mkdirs()) {
			System.out.println(outputTilesetDirectory + " was created.");
		}
		input.close();
		System.out.println("(Please wait a few seconds for the images to load.)");
		ArrayList<BufferedImage> images = addImagesFromDirectory(inputDirectory);
		BufferedImage tileset = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
		int imageIndex = 0;
		for (int y = 0; y < tileset.getHeight(); y += inputLength) {
			for (int x = 0; x < tileset.getWidth(); x += inputLength) {
				for (int imageY = 0; imageY < inputLength; imageY++) {
					int defaultARGBValue = -16777216;
					for (int imageX = 0; imageX < inputLength; imageX++) {
						if (imageIndex < images.size()) {
							tileset.setRGB(x + imageX, y + imageY, images.get(imageIndex).getRGB(imageX, imageY));
						} else {
							tileset.setRGB(x + imageX, y + imageY, defaultARGBValue);
						}
					}
				}
				if (imageIndex < images.size()) {
					imageIndex++;
				}
			}
		}
		try {
			ImageIO.write(tileset, "png", new FileOutputStream(outputTilesetLocation));
		} catch (Exception e) {
			error("Unable to create " + outputTilesetLocation + ".");
		}
		if (imageIndex == 1) {
			System.out.println("Success: " + outputTilesetLocation + " was created from " + imageIndex + " image!");
		} else {
			System.out.println("Success: " + outputTilesetLocation + " was created from " + imageIndex + " images!");
		}
	}
	
	private static ArrayList<BufferedImage> addImagesFromDirectory(File directory) {
		ArrayList<BufferedImage> images = new ArrayList<>();
		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				BufferedImage image;
				try {
					image = ImageIO.read(file);
				} catch (Exception e) {
					image = null;
				}
				if (image == null) {
					System.out.println(file.getPath() + " does not contain a readable image, and is being skipped.");
					continue;
				}
				if (image.getWidth() != inputLength || image.getHeight() != inputLength) {
					System.out.println(file.getPath() + " has an invalid resolution of " +
							image.getWidth() + "x" + image.getHeight() + " pixels, and is being skipped.");
					continue;
				}
				images.add(image);
			} else if (file.isDirectory()) {
				images.addAll(addImagesFromDirectory(file));
			}
		}
		return images;
	}
	
	private static void error(String message) {
		System.out.println("Error: " + message);
		System.exit(1);
	}
}
