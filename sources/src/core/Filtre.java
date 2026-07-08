package core;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class Filtre extends FileFilter
{
  public boolean accept(File f)
  {
      return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
  }

  public String getDescription()
  {
      return "Configuration RCC (*.XML)";
  }
}