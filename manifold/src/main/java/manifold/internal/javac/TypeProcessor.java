package manifold.internal.javac;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import manifold.api.type.ITypeManifold;
import manifold.api.type.ITypeProcessor;
import manifold.internal.host.ManifoldHost;

/**
 */
public class TypeProcessor extends CompiledTypeProcessor
{
  TypeProcessor( JavacTask javacTask )
  {
    super( javacTask );
  }

//  @Override
//  public boolean filterError( Diagnostic diagnostic )
//  {
//    for( ITypeManifold sp: ManifoldHost.getCurrentModule().getTypeManifolds() )
//    {
//      if( sp instanceof ITypeProcessor )
//      {
//        if( ((ITypeProcessor)sp).filterError( this, diagnostic ) )
//        {
//          return true;
//        }
//      }
//    }
//    return false;
//  }

  @Override
  public void process( TypeElement element, TreePath tree, IssueReporter<JavaFileObject> issueReporter )
  {
    String fqn = element.getQualifiedName().toString();
    for( ITypeManifold sp : ManifoldHost.getCurrentModule().getTypeManifolds() )
    {
      if( sp instanceof ITypeProcessor )
      {
        JavacProcessingEnvironment.instance( getContext() ).getMessager().printMessage( Diagnostic.Kind.NOTE, "Processing: " + element.getQualifiedName() );

        ((ITypeProcessor)sp).process( fqn, this, issueReporter );
      }
    }
  }
}
