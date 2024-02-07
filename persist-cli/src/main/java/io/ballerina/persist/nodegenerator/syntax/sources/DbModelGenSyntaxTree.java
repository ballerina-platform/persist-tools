package io.ballerina.persist.nodegenerator.syntax.sources;


import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.persist.BalException;
import io.ballerina.persist.models.Entity;
import io.ballerina.persist.models.Module;
import io.ballerina.persist.nodegenerator.syntax.utils.BalSyntaxUtils;


import java.util.Collection;


public class DbModelGenSyntaxTree implements IntrospectSyntaxTree {

    @Override
    public SyntaxTree getDataModels(Module entityModule) throws BalException {
        Collection<Entity> entityArray = entityModule.getEntityMap().values();
        if (entityArray.size() != 0) {
            return BalSyntaxUtils.generateModelSyntaxTree(entityModule);
        }
        return null;
    }

    @Override
    public SyntaxTree createInitialDriverImportFile() throws BalException {
        return BalSyntaxUtils.createDriverImportFile("mysql");
    }


}
