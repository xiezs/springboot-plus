/*
 * @Author: 一日看尽长安花
 * @since: 2020-03-16 11:16:52
 * @LastEditTime: 2020-03-16 13:13:31
 * @LastEditors: 一日看尽长安花
 * @Description:
 */
import request from '@/utils/request';

/**
 * 一些常量
 */
export const IMG_MIME = 'image/*';
export const DOC_MIME =
  '.doc,.docx,.xml,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document';

/**
 * 手动上传附件
 */
export function uploadAttachment(data) {
  return request({
    url: '/core/file/uploadAttachment',
    method: 'post',
    data
  });
}
/**
 * 下载多附件中的一个附件
 * @param {object} params 包含的参数：fileId,batchFileUUID
 */
export function downloadMutipleFile(params) {
  const { fileId, batchFileUUID } = { ...params };
  return request({
    url: `/core/file/download/${fileId}/${batchFileUUID}`,
    method: 'get'
  });
}

/**
 * 通过path下载单个文件
 *
 * @param {object} params 包含的参数：path
 */
export function download(params) {
  return request({
    url: '/core/file/download',
    method: 'get',
    params
  });
}

/**
 * 获取附件信息
 * @param {object} params 包含fileBatchId
 */
export function getFileList(params) {
  return request({
    url: '/core/file/items',
    method: 'get',
    params
  });
}
