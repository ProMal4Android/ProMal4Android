# Generated by Django 4.0.1 on 2022-11-08 12:13

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('common', '0006_delete_addapitest_delete_apitest_delete_kgtest_and_more'),
    ]

    operations = [
        migrations.CreateModel(
            name='AddApiTest',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('listID', models.IntegerField()),
                ('list', models.CharField(max_length=600)),
            ],
        ),
        migrations.CreateModel(
            name='ApiTest',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('apiID', models.IntegerField()),
                ('apiName', models.CharField(max_length=200)),
                ('inLevel', models.IntegerField()),
                ('outLevel', models.IntegerField()),
                ('addList', models.IntegerField()),
                ('repList', models.IntegerField()),
            ],
        ),
        migrations.CreateModel(
            name='KgTest',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('nodeID', models.IntegerField()),
                ('actionName', models.CharField(max_length=120)),
                ('perList', models.CharField(max_length=120, null=True)),
                ('apiList', models.CharField(max_length=120, null=True)),
            ],
        ),
        migrations.CreateModel(
            name='PerTest',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('perID', models.IntegerField()),
                ('perName', models.CharField(max_length=200)),
                ('inLevel', models.IntegerField()),
                ('outLevel', models.IntegerField()),
            ],
        ),
        migrations.CreateModel(
            name='RepApiTest',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('listID', models.IntegerField()),
                ('list', models.CharField(max_length=600)),
            ],
        ),
    ]
