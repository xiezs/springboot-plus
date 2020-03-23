package com.ibeetl.admin.core.web;

import cn.hutool.core.util.StrUtil;
import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.file.FileItem;
import com.ibeetl.admin.core.file.FileService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.FileUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * 描述：文件上传下载操作
 *
 * @author 一日看尽长安花 Created on 2020/3/16
 */
@Slf4j
@Controller
@RequestMapping("/core/file")
public class FileSystemElContorller {

  @Autowired CorePlatformService platformService;

  @Autowired FileService fileService;

  /**
   * 上传文件
   *
   * @author 一日看尽长安花
   * @param fileBatchId 文件批次id，第一次上传应该自动生成一个返回给前端
   * @param bizId 有关的业务id
   * @param bizType 有关的业务类型type
   * @return JsonResult<String> 返回文件操作批次id
   * @throws IOException when
   */
  @PostMapping("/uploadAttachment")
  @ResponseBody
  public JsonResult<String> uploadFile(
      @RequestParam("file") MultipartFile file,
      @NotBlank String fileBatchId,
      String bizType,
      String bizId)
      throws IOException {
    if (file.isEmpty()) {
      return JsonResult.fail();
    }
    CoreUser user = platformService.getCurrentUser();
    CoreOrg org = platformService.getCurrentOrg();
    FileItem fileItem =
        fileService.createFileItem(
            file.getOriginalFilename(),
            bizType,
            bizId,
            user.getId(),
            org.getId(),
            fileBatchId,
            null);
    OutputStream os = fileItem.openOutpuStream();
    FileUtil.copy(file.getInputStream(), os);
    return JsonResult.success(fileBatchId);
  }

  @PostMapping("/deleteAttachment")
  @ResponseBody
  public JsonResult deleteFile(Long fileId, String batchFileUUID) {
    fileService.removeFile(fileId, batchFileUUID);
    return JsonResult.success();
  }

  /**
   * 下载多文件中的某一个文件，
   *
   * @author 一日看尽长安花
   * @param batchFileUUID 多文件的文件操作批次id
   * @param fileId 多文件中某个文件的id
   * @throws IOException when
   */
  @GetMapping("/download/{fileId}/{batchFileUUID}")
  public void downloadMutipleFile(
      HttpServletResponse response, @PathVariable Long fileId, @PathVariable String batchFileUUID)
      throws IOException {
    FileItem item = fileService.getFileItemById(fileId, batchFileUUID);
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=" + URLEncoder.encode(item.getName(), "UTF-8"));
    item.copy(response.getOutputStream());
  }

  /*execl 导入导出*/

  /**
   * 通过路径下载文件
   *
   * @author 一日看尽长安花
   * @param path 文件path路径
   * @throws IOException when
   */
  @GetMapping("/download")
  public void downloadFileByPath(HttpServletResponse response, String path) throws IOException {
    response.setContentType("text/html; charset = UTF-8");
    FileItem fileItem = fileService.loadFileItemByPath(path);
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=" + java.net.URLEncoder.encode(fileItem.getName(), "UTF-8"));
    fileItem.copy(response.getOutputStream());
    if (fileItem.isTemp()) {
      fileItem.delete();
    }
  }

  @GetMapping("/downloadTemplate")
  public void dowloadTemplate(HttpServletResponse response, String path) throws IOException {
    response.setContentType("text/html; charset = UTF-8");
    int start1 = path.lastIndexOf("\\");
    int start2 = path.lastIndexOf("/");
    if (start2 > start1) {
      start1 = start2;
    }
    String file = path.substring(start1 + 1);
    response.setHeader(
        "Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(file, "UTF-8"));
    InputStream input =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("excelTemplates/" + path);
    FileUtil.copy(input, response.getOutputStream());
  }

  /**
   * 通过文件批次操作id，返回文件列表信息
   *
   * @author 一日看尽长安花
   * @return JsonResult<List < LocalFileItem>>
   */
  @GetMapping("/items")
  @ResponseBody
  public JsonResult<List<FileItem>> fileItemList(String fileBatchId) {
    if (StrUtil.isBlank(fileBatchId)) {
      return JsonResult.success();
    }
    List<FileItem> fileItems = fileService.queryByBatchId(fileBatchId);
    return JsonResult.success(fileItems);
  }
}
