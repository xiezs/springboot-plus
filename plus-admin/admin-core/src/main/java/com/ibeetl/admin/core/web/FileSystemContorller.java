package com.ibeetl.admin.core.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ibeetl.admin.core.entity.CoreOrg;
import com.ibeetl.admin.core.entity.CoreUser;
import com.ibeetl.admin.core.file.FileItem;
import com.ibeetl.admin.core.file.FileService;
import com.ibeetl.admin.core.service.CorePlatformService;
import com.ibeetl.admin.core.util.FileUtil;

/**
 * Class FileSystemContorller : <br/>
 * 描述：业务中有关文件的上传，下载。<br/>
 * TODO 待改动计划：
 *  改成加上MD5验证，减少重复文件的io
 *  补上文件表中有关文件属性（大小，类型，）的信息
 *  补上文件上传的临时状态，避免上传文件却中断业务逻辑，产生无用数据以及文件
 * @author 一日看尽长安花
 * Updated on 2020/3/8
 */
@Slf4j
@Controller
@RequestMapping("/core/file")
public class FileSystemContorller {

  @Autowired CorePlatformService platformService;

  @Autowired FileService fileService;

  /*附件类操作*/
  @PostMapping("/uploadAttachment.json")
  @ResponseBody
  public JsonResult uploadFile(
      @RequestParam("file") MultipartFile file, String batchFileUUID, String bizType, String bizId)
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
            batchFileUUID,
            null);
    OutputStream os = fileItem.openOutpuStream();
    FileUtil.copy(file.getInputStream(), os);
    return JsonResult.success(fileItem);
  }

  @PostMapping("/deleteAttachment.json")
  @ResponseBody
  public JsonResult deleteFile(Long fileId, String batchFileUUID) throws IOException {
    fileService.removeFile(fileId, batchFileUUID);
    return JsonResult.success();
  }

  @GetMapping("/download/{fileId}/{batchFileUUID}/{name}")
  public ModelAndView download(
      HttpServletResponse response, @PathVariable Long fileId, @PathVariable String batchFileUUID)
      throws IOException {
    FileItem item = fileService.getFileItemById(fileId, batchFileUUID);
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=" + URLEncoder.encode(item.getName(), "UTF-8"));
    item.copy(response.getOutputStream());
    return null;
  }

  /*execl 导入导出*/

  @GetMapping("/get.do")
  public ModelAndView index(HttpServletResponse response, String id) throws IOException {
    String path = id;
    response.setContentType("text/html; charset = UTF-8");
    FileItem fileItem = fileService.loadFileItemByPath(path);
    response.setHeader(
        "Content-Disposition",
        "attachment; filename=" + java.net.URLEncoder.encode(fileItem.getName(), "UTF-8"));
    fileItem.copy(response.getOutputStream());
    if (fileItem.isTemp()) {
      fileItem.delete();
    }
    return null;
  }

  @GetMapping("/downloadTemplate.do")
  public ModelAndView dowloadTemplate(HttpServletResponse response, String path)
      throws IOException {
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
    return null;
  }

  @GetMapping("/simpleUpload.do")
  public ModelAndView simpleUploadPage(String uploadUrl, String templatePath, String fileType)
      throws IOException {
    ModelAndView view = new ModelAndView("/common/simpleUpload.html");
    view.addObject("uploadUrl", uploadUrl);
    view.addObject("templatePath", templatePath);
    view.addObject("fileType", fileType);

    return view;
  }
}
