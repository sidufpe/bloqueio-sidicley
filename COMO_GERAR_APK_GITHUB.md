
# Como pegar seu APK compilado (LINK)

### Passo 1: Criar repositório no GitHub
1. Vá em github.com/new
2. Nome: `bloqueio-chamada-sidicley`
3. Deixe público
4. Não marque README

### Passo 2: Enviar este projeto
Descompacte o ZIP e no terminal dentro da pasta:
```bash
git init
git add .
git commit -m "App Sidicley inicial"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/bloqueio-chamada-sidicley.git
git push -u origin main
```

### Passo 3: Pegar o LINK do APK
1. No seu repositório no GitHub, clique na aba **Actions**
2. Clique no workflow **Build APK Sidicley** > **Run workflow**
3. Espere 2-3 minutos
4. No final da execução, vai aparecer em **Artifacts**: `app-sidicley-apk`
5. Clique para baixar - dentro está o `app-debug.apk` PRONTO PARA INSTALAR

> O link do artefato é o seu APK compilado na nuvem, gratuito!

### Instalar no celular
- Transfira o app-debug.apk para o celular
- Abra e permita "Instalar apps desconhecidos"
- Abra o app > Conceder Permissões > Aceitar como App de Filtragem
