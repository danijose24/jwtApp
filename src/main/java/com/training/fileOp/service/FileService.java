package com.training.fileOp.service;

import com.training.fileOp.entity.FileOP;

import java.util.List;

public interface FileService {
    void saveFileOP(FileOP files);
    List<FileOP> getAllFiles();
}
