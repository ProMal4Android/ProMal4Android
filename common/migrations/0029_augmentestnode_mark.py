# Generated by Django 4.1.7 on 2023-05-17 06:37

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('common', '0028_alter_augmentestapi_apiid_alter_augmentestper_perid'),
    ]

    operations = [
        migrations.AddField(
            model_name='augmentestnode',
            name='mark',
            field=models.CharField(blank=True, default='', max_length=50),
        ),
    ]
