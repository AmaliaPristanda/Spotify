from sqlalchemy import Column, String, Integer
from base.sql_base import Base

#descriptorii tabelul din perpesctiva python
#trebuie sa descriem exact coloanele pe care le avem si tipurile asociate
class Blacklist(Base):
    __tablename__ = 'blacklist'

    ID = Column(Integer, primary_key=True)
    token = Column(String)

    def __init__(self, token):
        self.token = token