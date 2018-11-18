from app import db

class light_config(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=True, unique=True)
    values = db.relationship('light_vals', backref='setting', lazy='dynamic')

    def __repr__(self):
        return '<Config {}>'.format(self.name)

class light_vals(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    color = db.Column(db.Integer, index=True)
    config_name = db.Column(db.Integer, db.ForeignKey('light_config.id'))

    def __repr__(self):
        return '<Color {}>'.format(self.color)