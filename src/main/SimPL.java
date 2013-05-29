package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import semantics.InterpretVisitor;
import semantics.SimPLTypes;
import utils.InterpretException;
import utils.SimPLDumper;
import utils.TypeException;

import javacc.*;

public class SimPL {

	
	public static void main(String args[]) throws ParseException, TypeException, InterpretException, IOException {
		SimPLParser parser = new SimPLParser(System.in);
		
		Scanner in = new Scanner(System.in);
		if (args.length == 0
				|| (args.length == 1 && args[0].equals("-s"))) {
			do
			{

				System.out.print("SimPL > ");
				
				parser.ReInit(System.in);
				String program = "";
				boolean breakOuter = false;
				char input = 0;
				do
				{
					String line = in.nextLine();

					for(int i = 0; i < line.length(); ++i)
					{
						input = line.charAt(i);
						
						if('$' == input)
						{
							breakOuter = true;
							break;
						}
						program += input;
					}
					program += " \n";
					if(breakOuter)
						break;
					System.out.print("      > ");
				}while(true);
				
				
				
				try {
					parser.ReInit(new ByteArrayInputStream(
							program.getBytes()));
					SimpleNode astTree = parser.Start();
					
					SimpleNode v = (SimpleNode)astTree.jjtAccept(new InterpretVisitor(), null);
					
					System.out.println(v.toString());

				} catch (ParseException e) {
					System.out.println(e.getMessage());
					System.out.println("Encountered errors during parse.");
				} catch (TypeException e)
				{
					System.out.println(e.getMessage());
					System.out.println("Encountered errors during type checking.");
				} catch (InterpretException e)
				{
					System.out.println(e.getMessage());
					System.out.println("Encountered errors during interpreting.");
				} catch (Exception e)
				{
					System.out.println(e.getMessage());
				} catch (Error e)
				{
					System.out.println(e.getMessage());
				}
				
			}while(true);
			
			
		} else if (args.length == 2 & args[0].equals("-f")) {
			//System.out.println("Reading from file " + args[1] + " . . .");
			String inputFilename = new String(args[1]);
			
			
			try {
				if(!inputFilename.endsWith(".spl") || inputFilename.length() <= 4)
				{
					throw new Exception("Input file should be end with .spl and formats like sample.spl");
				}
				String simplProgram = "";
				BufferedReader br = new BufferedReader(new FileReader(args[1]));
				while (true) {
					String line = br.readLine();
					if (null == line) {
						break;
					}

					simplProgram += line + " \n";
				}

				simplProgram = simplProgram.replace('$', ' ');

				parser.ReInit(new ByteArrayInputStream(
						simplProgram.getBytes()));
				
			} catch (FileNotFoundException e) {
				System.out.println("File " + args[1] + " not found.");
				return;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Error reading " + args[1]);
				return;
			} catch (Error e)
			{
				System.out.print(e.getMessage());
			}
		} else {
			System.out.println("SimPL Parser:  Usage is one of:");
			System.out.println("java -jar SimPL.jar");
			System.out.println("java -jar SimPL.jar -s");
			System.out.println("java -jar SimPL.jar -f FILE");
			return;
		}

		try {
			SimpleNode astTree = parser.Start();
			
			SimpleNode v = (SimpleNode) astTree.jjtAccept(new InterpretVisitor(), null);
			
			System.out.println(v.toString());
			
			String outputFilename = args[1].replaceAll(".spl", ".rst");
			FileWriter fw = new FileWriter(outputFilename);
			fw.write(v.toString());
			fw.close();
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			System.out.println("Encountered errors during parse.");
		}
//		catch (TypeException e)
//		{
//			System.out.println(e.getMessage());
//			System.out.println("Encountered errors during type checking.");
//		}
//		catch (InterpretException e)
//		{
//			System.out.println(e.getMessage());
//			System.out.println("Encountered errors during interpreting.");
//		} 
//		catch (Exception e)
//		{
//			System.out.println(e.getMessage());
//		} catch (Error e)
//		{
//			System.out.println(e.getMessage());
//		}
		
	}


}
