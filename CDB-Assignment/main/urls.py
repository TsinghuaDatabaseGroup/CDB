from django.conf.urls import url

from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^upload/$', views.upload),
    url(r'^check/$', views.check),
    url(r'^results/$', views.results),
]

