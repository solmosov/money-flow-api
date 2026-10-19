package com.example.moneyflowapi.export.service;

import com.example.moneyflowapi.exception.BusinessException;
import com.example.moneyflowapi.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;



@Component
public class ExportFileChecker {
    private final Path root;

    public ExportFileChecker(@Value("${app.export.dir:/var/app/exports}") String dir) {
        this.root = Path.of(dir).toAbsolutePath().normalize();
    }

    public void ensureServable(String fileName) {
        Path path = root.resolve(fileName).normalize();
        if (!path.startsWith(root) || !Files.isRegularFile(path)) {
            throw new BusinessException(ErrorCode.EXPORTS_FILE_MISSING);
        }
        try {
            if (Files.size(path) == 0 || !Files.isReadable(path)) {
                throw new BusinessException(ErrorCode.EXPORTS_FILE_UNREADABLE);
            }
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
            boolean webReadable = perms.contains(PosixFilePermission.OTHERS_READ)
                    || perms.contains(PosixFilePermission.GROUP_READ);
            if (!webReadable) {
                throw new BusinessException(ErrorCode.EXPORTS_FILE_PERMISSION_DENIED);
            }
        } catch (IOException | UnsupportedOperationException e) {
            throw new BusinessException(ErrorCode.EXPORTS_FILE_UNREADABLE);
        }
    }
}
