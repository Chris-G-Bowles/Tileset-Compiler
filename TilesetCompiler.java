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
		if (args.length == 0 || args.length == 3) {
			Scanner input = new Scanner(System.in);
			String inputDirectoryLocation;
			if (args.length == 0) {
				System.out.print("Enter the input directory's location: ");
				inputDirectoryLocation = input.nextLine();
			} else {
				inputDirectoryLocation = args[0];
			}
			String inputResolutionOption;
			if (args.length == 0) {
				System.out.println("Select the input resolution for the images:");
				System.out.println("1) 8x8 pixels");
				System.out.println("2) 16x16 pixels");
				System.out.println("3) 32x32 pixels");
				System.out.println("4) 64x64 pixels");
				System.out.print("Input resolution option: ");
				inputResolutionOption = input.nextLine();
			} else {
				inputResolutionOption = args[1];
			}
			String outputTilesetLocation;
			if (args.length == 0) {
				System.out.print("Enter the output tileset's location: ");
				outputTilesetLocation = input.nextLine();
			} else {
				outputTilesetLocation = args[2];
			}
			input.close();
			File inputDirectory = new File(inputDirectoryLocation);
			if (inputDirectory.isDirectory()) {
				if (isValidInteger(inputResolutionOption) && Integer.parseInt(inputResolutionOption) >= 1 &&
						Integer.parseInt(inputResolutionOption) <= 4) {
					inputLength = (int)Math.pow(2, Integer.parseInt(inputResolutionOption) + 2);
					System.out.println("(Please wait a few seconds for the images to load.)");
					ArrayList<BufferedImage> images = addImagesFromDirectory(inputDirectory);
					BufferedImage tileset = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
					int imageIndex = 0;
					for (int y = 0; y < tileset.getHeight(); y += inputLength) {
						for (int x = 0; x < tileset.getWidth(); x += inputLength) {
							for (int imageY = 0; imageY < inputLength; imageY++) {
								for (int imageX = 0; imageX < inputLength; imageX++) {
									if (imageIndex < images.size()) {
										tileset.setRGB(x + imageX, y + imageY,
												images.get(imageIndex).getRGB(imageX, imageY));
									} else {
										tileset.setRGB(x + imageX, y + imageY, -1);
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
						if (imageIndex == 1) {
							System.out.println("Success: " + outputTilesetLocation + " was created from " +
									imageIndex + " image!");
						} else {
							System.out.println("Success: " + outputTilesetLocation + " was created from " +
									imageIndex + " images!");
						}
					} catch (Exception e) {
						System.out.println("Error: Could not create " + outputTilesetLocation + ".");
					}
				} else {
					System.out.println("Error: Invalid input resolution option.");
				}
			} else {
				System.out.println("Error: " + inputDirectory + " is not a valid directory.");
			}
		} else {
			System.out.println("This program's usage is as follows:");
			System.out.println("java TilesetCompiler");
			System.out.println("java TilesetCompiler <input directory location> <input resolution option> " +
					"<output tileset location>");
		}
	}
	
	private static boolean isValidInteger(String string) {
		if (string.length() >= 2 && string.length() <= 10 && string.charAt(0) == '-') {
			for (int i = 1; i < string.length(); i++) {
				if (string.charAt(i) < '0' || string.charAt(i) > '9') {
					return false;
				}
			}
			return true;
		} else if (string.length() >= 1 && string.length() <= 9) {
			for (int i = 0; i < string.length(); i++) {
				if (string.charAt(i) < '0' || string.charAt(i) > '9') {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private static ArrayList<BufferedImage> addImagesFromDirectory(File directory) {
		File[] files = directory.listFiles();
		ArrayList<BufferedImage> images = new ArrayList<>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				try {
					BufferedImage image = ImageIO.read(files[i]);
					if (image.getWidth() == inputLength && image.getHeight() == inputLength) {
						images.add(image);
					} else {
						System.out.println("Error: " + files[i].getPath() + " has an invalid resolution of " +
								image.getWidth() + "x" + image.getHeight() + " pixels.");
					}
				} catch (Exception e) {
					System.out.println("Error: " + files[i].getPath() + " does not contain a readable image.");
				}
			} else if (files[i].isDirectory()) {
				images.addAll(addImagesFromDirectory(files[i]));
			}
		}
		return images;
	}
	
	private static void error(String message) {
		System.out.println("Error: " + message);
		System.exit(1);
	}
}
