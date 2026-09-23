
# Bloqueio de Chamada Sidicley

App Android completo que bloqueia QUALQUER ligação cujo número NÃO esteja na agenda.

### Funcionalidades
- **Interruptor Ativo**: liga/desliga bloqueio
- **Modos**:
  1. Bloquear quem NÃO está na agenda (igual ao iOS Silenciar Desconhecidos + Truecaller modo lista branca)【4248741579783736442†L53-L59】
  2. Só permitir lista branca (agenda + números que você adicionou)
  3. Bloquear só lista negra (estilo Call Blocker Free / Blacklist Plus)
- **Listar bloqueadas**: histórico com data, motivo
- **Adicionar número** manualmente em lista branca ou negra
- **Banco local Room** - sem internet, 100% privado

### Como instalar
1. Abra no Android Studio (Giraffe+)
2. Sync Gradle
3. Rode no celular físico (CallScreeningService não funciona no emulador)
4. Conceda permissões: Contatos, Telefone, Registro de chamadas
5. Aceite ser "App de filtragem de chamadas" (ROLE_CALL_SCREENING) - obrigatório no Android 10+

### Comparado aos concorrentes pesquisados:
- Truecaller, Mr. Number, Whoscall, Hiya【4248741579783736442†L6-L9】 usam lista negra + ID de chamador online. Nosso app faz igual ao modo "Bloqueia desconhecido (Bloqueia chamadas que não estão na lista de contactos)" do Bloqueador de Chamadas da Play Store【4248741579783736442†L57-L59】 mas sem anúncios e offline.
- Android nativo já tem "Bloquear números desconhecidos"【4248741579783736442†L26-L29】, mas não lista histórico. Aqui listamos.

### Arquivos principais
- SidicleyCallScreeningService.kt = motor de bloqueio
- MainActivity.kt = UI de configuração
- Database.kt = Room

Se quiser APK: no Android Studio > Build > Build APK(s)
