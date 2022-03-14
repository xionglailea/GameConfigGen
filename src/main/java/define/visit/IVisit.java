package define.visit;

import define.type.*;

public interface IVisit {

    void visit(IInt intType);

    void visit(IBean beanType);

    void visit(IBoolean boolType);

    void visit(IEnum enumType);

    void visit(IFloat floatType);

    void visit(IList listType);

    void visit(ILong longType);

    void visit(IMap mapType);

    void visit(IString stringType);

}
