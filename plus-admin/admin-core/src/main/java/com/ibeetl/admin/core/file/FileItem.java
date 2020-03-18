package com.ibeetl.admin.core.file;

import java.io.OutputStream;
import lombok.Data;

@Data
public abstract class FileItem {
  protected Long id;
  protected String name;
  protected String path;
  boolean isTemp = false;

  public abstract OutputStream openOutpuStream();

  public abstract void copy(OutputStream os);

  public abstract boolean delete();
}
