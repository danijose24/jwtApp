package com.training.fileOp.serviceImpl;

import com.training.fileOp.entity.FileOP;
import com.training.fileOp.repository.FileRepository;
import com.training.fileOp.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;
    /**
     * Method to Save the File Details in the Database
     * @param fileOP
     */
    @Override
    public void saveFileOP(FileOP fileOP) {
        fileRepository.save(fileOP);
    }

    /**
     * Method to get the All the Files
     * @return list of Files
     */
    @Override
    public List<FileOP> getAllFiles() {
        return fileRepository.findAll();
    }
}
