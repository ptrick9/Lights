from flask_wtf import FlaskForm
from wtforms import StringField, PasswordField, BooleanField, SubmitField, IntegerField, FieldList, FormField
from wtforms.validators import DataRequired

class ColorEntryForm(FlaskForm):
    color = IntegerField()

class ColorsForm(FlaskForm):
    config_name = StringField('Username', validators=[DataRequired()])
    #password = PasswordField('Password', validators=[DataRequired()])
    colors = FieldList(FormField(ColorEntryForm), min_entries=2)
    #remember_me = BooleanField('Remember Me')
    submit = SubmitField('Sign In')