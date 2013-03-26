package org.newdawn.slick.tools.packulike;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

/**
 * A daft image packer
 * 
 * @author kevin
 */
public class Pack {
	/**
	 * Pack the images provided
	 * 
	 * @param files The list of file objects pointing at the images to be packed
	 * @param width The width of the sheet to be generated 
	 * @param height The height of the sheet to be generated
	 * @param border The border between sprites
	 * @param out The file to write out to
	 * @return The generated sprite sheet
	 * @throws IOException Indicates a failure to write out files
	 */
	public Sheet pack(ArrayList files, int width, int height, int border, File out) throws IOException {
		ArrayList images = new ArrayList();
		
		try {
			for (int i=0;i<files.size();i++) {
				File file = (File) files.get(i);
				Sprite sprite = new Sprite(file.getName(), ImageIO.read(file));
				
				images.add(sprite);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return packImages(images, width, height, border, out);
	}

	/**
	 * Pack the images provided
	 * 
	 * @param images The list of sprite objects pointing at the images to be packed
	 * @param width The width of the sheet to be generated 
	 * @param height The height of the sheet to be generated
	 * @param border The border between sprites
	 * @param out The file to write out to
	 * @return The generated sprite sheet
	 * @throws IOException Indicates a failure to write out files
	 */
	public Sheet packImages(ArrayList images, int width, int height, int border, File out) throws IOException {
		Collections.sort(images, new Comparator() {
			public int compare(Object o1, Object o2) {
				Sprite a = (Sprite) o1;
				Sprite b = (Sprite) o2;
				
				int asize = a.getHeight();
				int bsize = b.getHeight();
				return bsize - asize;
			}
		});
		
		int x = 0;
		int y = 0;
		
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = result.getGraphics();
		int rowHeight = 0;
		
		try {
			PrintStream pout = null;
			if (out != null) {
				pout = new PrintStream(new FileOutputStream(new File(out.getParentFile(), out.getName()+".xml")));
				pout.println("<sheet>");
			}
			
			for (int i=0;i<images.size();i++) {
				Sprite current = (Sprite) images.get(i);
				if (x + current.getWidth() > width) {
					x = 0;
					y += rowHeight;
					rowHeight = 0;
				}
				
				if (rowHeight == 0) {
					rowHeight = current.getHeight() + border;
				}
				
				if (out != null) {
					pout.print("\t<sprite ");
					pout.print("name=\""+current.getName()+"\" ");
					pout.print("x=\""+x+"\" ");
					pout.print("y=\""+y+"\" ");
					pout.print("width=\""+current.getWidth()+"\" ");
					pout.print("height=\""+current.getHeight()+"\" ");
					pout.println("/>");
				}
				
				current.setPosition(x,y);
				g.drawImage(current.getImage(), x, y, null);
				x += current.getWidth() + border;
			}
			g.dispose();
			
			if (out != null) {
				pout.println("</sheet>");
				pout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			IOException io = new IOException("Failed writing image XML");
			io.initCause(e);
			
			throw io;
		}
		
		if (out != null) {
			try {
				ImageIO.write(result, "PNG", out);
			} catch (IOException e) {
				e.printStackTrace();
				
				IOException io = new IOException("Failed writing image");
				io.initCause(e);
				
				throw io;
			}
		}
		
		return new Sheet(result, images);
	}
	
	/**
	 * Entry point to the tool, just pack the current directory of images
	 * 
	 * @param argv The arguments to the program
	 * @throws IOException Indicates a failure to write out files
	 */
	public static void main(String[] argv) throws IOException {
		File dir = new File(".");
		dir = new File("C:\\eclipse\\grobot-workspace\\anon\\res\\tiles\\indoor1");
		
		ArrayList list = new ArrayList();
		File[] files = dir.listFiles();
		for (int i=0;i<files.length;i++) {
			if (files[i].getName().endsWith(".png")) {
				if (!files[i].getName().startsWith("output")) {
					list.add(files[i]);
				}
			}
		}
		
		Pack packer = new Pack();
		packer.pack(list, 512, 512, 1, new File(dir, "output.png"));
		System.out.println("Output Generated.");
	}
}
