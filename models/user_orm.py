<<<<<<< HEAD
from sqlalchemy import Column, String, Integer
from base.sql_base import Base
from sqlalchemy.orm import relationship
from models.users_roles_orm import user_roles_relationship


class User(Base):
    #tabel name exact numele tabelei
    __tablename__ = 'Users'
    #exact numele de coloana din mysql il pun ca variabiala aici(case sensitive)
    ID = Column(Integer, primary_key=True)
    username = Column(String)
    password = Column(String)

    #user are relatie de 1 la multi cu roluri
    #in secondary spun ca elementele respective este tabela de join_ur
    #??? aici ramane la fel ???

    #ca sa inserez in tabela de join trebuie sa creez un nou user care are un array de roluri implicit
    roles = relationship("Role", secondary=user_roles_relationship)

    def __init__(self, username, password):
        self.username = username
        self.password = password
=======
from sqlalchemy import Column, String, Integer
from base.sql_base import Base
from sqlalchemy.orm import relationship
from models.users_roles_orm import user_roles_relationship


class User(Base):
    #tabel name exact numele tabelei
    __tablename__ = 'Users'
    #exact numele de coloana din mysql il pun ca variabiala aici(case sensitive)
    ID = Column(Integer, primary_key=True)
    username = Column(String)
    password = Column(String)

    #user are relatie de 1 la multi cu roluri
    #in secondary spun ca elementele respective este tabela de join_ur
    #??? aici ramane la fel ???

    #ca sa inserez in tabela de join trebuie sa creez un nou user care are un array de roluri implicit
    roles = relationship("Role", secondary=user_roles_relationship)

    def __init__(self, username, password):
        self.username = username
        self.password = password
>>>>>>> e7eabf4 (IDM module)
