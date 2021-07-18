from django.shortcuts import render
import os

def demo(request):
    return render(request, 'coloring/demo.html')

def mandala(request):
    return render(request, 'coloring/mandala.html')

def new_interaction(request,path='mandala-freepik'):
    context_dict = {}
    context_dict['imagePath']= '/static/coloring/images/' + path + '.svg'
    return render(request, 'coloring/new_interaction.html', context=context_dict)

def homepage(request):
    context_dict = {}
    files = os.listdir(os.path.join("coloring/static/", "coloring/images"))
    context_dict['files'] = [(x.split('.')[0],'coloring/images/'+x) for x in  files[0:]]
    return render(request, 'coloring/homepage.html', context=context_dict)

def settings(request):
    return render(request, 'coloring/settings.html')

def help(request):
    return render(request, 'coloring/help.html')

def clown(request):
    return render(request, 'coloring/clown.html')

def flower(request):
    return render(request, 'coloring/flower.html')

def simple(request):
    return render(request, 'coloring/simple.html')

def triangles(request):
    return render(request, 'coloring/triangles.html')

def butterfly(request):
    return render(request, 'coloring/butterfly.html')
