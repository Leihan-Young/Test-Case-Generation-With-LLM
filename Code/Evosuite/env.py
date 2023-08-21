import sys
from typing import Any

class _const:
    def __new__(cls, *args, **kw):
        if not hasattr(cls, '_instance'):
            orig = super(_const, cls)
            cls._instance = orig.__new__(cls, *args, **kw)
        return cls._instance
    
    class ConstError(TypeError):
        pass

    class ConstCaseError(ConstError):
        pass

    def __setattr__(self, __name: str, __value: Any) -> None:
        if __name in self.__dict__:
            raise self.ConstError("Can't change const.{}".format(__name))
        if not __name.isupper():
            raise self.ConstCaseError("Const name {} is not all uppercase".format(__name))
        self.__dict__[__name] = __value

sys.modules[__name__] = _const()