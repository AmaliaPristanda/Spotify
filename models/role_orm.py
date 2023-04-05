from sqlalchemy import Column, String, Integer
from base.sql_base import Base

#descriptorii tabelul din perpesctiva python
#trebuie sa descriem exact coloanele pe care le avem si tipurile asociate
class Role(Base):
    __tablename__ = 'Roles'

    ID = Column(Integer, primary_key=True)
    rolename = Column(String)

    def __init__(self, rolename):
        self.rolename = rolename
