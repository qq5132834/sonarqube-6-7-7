//package org.eclipse.photran.internal.tests;
//
//import org.eclipse.photran.internal.core.lexer.sourceform.UnpreprocessedFixedSourceForm;
//import org.eclipse.photran.internal.core.lexer.sourceform.UnpreprocessedFreeSourceForm;
//import org.eclipse.photran.internal.core.sourceform.ISourceForm;
//import org.eclipse.photran.internal.core.sourceform.SourceForm;
//
//import java.io.File;
//
//public abstract class PhotranASTTestCase {
//
//    protected File file = null;
//    protected boolean isFixedForm = false;
//    protected String fileDescription = null;
//
//    protected ISourceForm createSourceForm()
//    {
//        if (isFixedForm)
//            return new UnpreprocessedFixedSourceForm();
//        else if (file.getName().endsWith(".F90"))
//            return SourceForm.of(null, file.getPath()); // delegate to create CPP source form
//        else
//            return new UnpreprocessedFreeSourceForm();
//    }
//
//}
