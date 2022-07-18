package com.zuk.cdt.report;

import com.zuk.cdt.file.FileFrame;

import java.util.Map;
import java.util.Optional;

public interface DoReport {
    public void report(Map<String, Optional<FileFrame>> cppFileFrameSet);
}
