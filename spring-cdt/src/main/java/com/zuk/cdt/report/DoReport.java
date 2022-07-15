package com.zuk.cdt.report;

import com.zuk.cdt.file.CppFileFrame;

import java.util.Map;
import java.util.Optional;

public interface DoReport {
    public void report(Map<String, Optional<CppFileFrame>> cppFileFrameSet);
}
