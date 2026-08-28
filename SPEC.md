<!-- para IA. não é README de humano. -->
# SPEC — training

status: v0.6
sha: `3a35170`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`REGRA-` `DADOS-` `CONTRATO-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita REGRA/CONTRATO. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra REGRA/NÃO; senão vira GAP e só então código.
- GAP = pergunta aberta. Não trate GAP como regra.

## Papel
MS **interno** (porta `8087`). Dono do conteúdo: Guru → Training → Module / Subject → Video → Link. Sem auth HTTP. Dono da tabela de guru (não tem MS guru). Repo git permanece `digitus-forum-course-microservice`.

## REGRA
- REGRA-OWN-1: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-OWN-1.
- REGRA-TRAINING-OWN-1: Training tem `userId` (quem **cadastrou**, equipe). **Não** é o guru. retrieveById/delete só do dono (código após PR #5).
- REGRA-OWN-2: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-OWN-2.
- REGRA-TRAINING-OWN-2: Module/Subject/Video carregam `userId` (e trainingId quando couber). Video/Training também têm `perfilId`.
- REGRA-TREE-1: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-TREE-1.
- REGRA-TRAINING-TREE-1: Module pertence a um Training. Subject pertence a um Training. Video liga-se a Module via `ModuleVideo` (position) e/ou a Subject via `SubjectVideo`.
- REGRA-LINK-1: Link pertence a um Video (`videoId`, `name`, `url`, `position`).
- REGRA-DEL-1: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-DEL-1.
- REGRA-TRAINING-DEL-1: Training/Subject/Video têm flag `deleted` nas **leituras**. O CONTRATO `delete` de Training/Module/Video/Link hoje é **hard delete** (não flip da flag). Module/Link/joins não têm `deleted`.
- REGRA-ID-1: ids UUID.
- REGRA-GURU-1: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-GURU-1.
- REGRA-TRAINING-GURU-1: Training tem `guruId`. Training sem guru **não existe**.
- REGRA-GURU-2: lançamento só o guru `slug=java`. Este front é a vitrine (um front para todos os gurus depois).
- REGRA-GURU-3: um domínio, um front. Guru **não** ganha site próprio.
- REGRA-GURU-USER: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-GURU-USER.
- REGRA-TRAINING-GURU-USER: aluno é **global** (vive no user MS). Um `userId` estuda com vários gurus. Training/Guru **não** donos do aluno.
- REGRA-GURU-4: **revogado** (2026-08-28). Course vira Training. Ver REGRA-TRAINING-GURU-4.
- REGRA-TRAINING-GURU-4: cada guru tem treinamentos; cada treinamento é grátis ou pago (`DADOS-TRAINING.paid` + `price`). Gratuito = paid=false AND price=0.
- REGRA-MVP1-GURU-SHOW: sistema aceita N gurus; MVP1 **mostra** só `java`.
- REGRA-GURU-PAGE-1: menu esquerdo do guru = DADOS-GURU-PAGE ordenado por `position`. Arquivo estático noutro host.
- REGRA-AUDIO-1: áudio da aula no path `{videoId}.m4a`; front baixa inteiro.

## NÃO
- NÃO-EXPOSE
- NÃO-SHUTDOWN
- NÃO-I18: texto de UI e variação pt/en **não** vivem aqui (vão pro i18n). Training não duplica por idioma. Nome visível do guru = i18n, não coluna de display.
- NÃO-LOCALE: Training **não** tem `locale` nem `familyId`.
- NÃO-YOUTUBE: `url` e `thumbnail` em Video são legado (YouTube). **Remover.** Aula = `gif` (+ áudio).
- NÃO-BACKOFFICE: sem create/update público de guru; sem UI admin agora. Sistema **aceita** gurus (tabela + `guruId` no Training). Operadores inserem; produto não expõe CRUD de guru na borda.
- NÃO-GURU-HOST: sem host por guru.

## DADOS
| id | tabela | campos |
|---|---|---|
| DADOS-GURU | Guru | guruId, slug (`java` no lançamento), deleted. Nome visível = i18n. |
| DADOS-GURU-PAGE | GuruPage | guruPageId, guruId, titleKey (i18n), src (path HTML estático), position, deleted. HTML **não** no banco. |
| DADOS-COURSE | Course | **revogado** (2026-08-28). Entidade vira Training. Ver DADOS-TRAINING. Migração SQL: Ricardo. |
| DADOS-TRAINING | Training | trainingId, guruId (string, MVP1=`java`), userId (equipe), perfilId, name, sinopse, description, paid (boolean), price (integer centavos BRL, avulso daquele training), deleted. Gratuito = paid=false AND price=0. Java Junior = gratuito. JPA `@Table(name = "training")`. |
| DADOS-MOD | Module | moduleId, trainingId, userId, name, sinopse, number, newNumber, description |
| DADOS-SUB | Subject | subjectId, trainingId, userId, name, sinopse, description, deleted |
| DADOS-VID | Video | videoId, userId, perfilId, name, sinopse, description, gif, deleted. **Não** url/thumbnail. Áudio = `buckets/digitus-forum-media/videos/{videoId}.m4a` (arquivo inteiro no front). |
| DADOS-MV | ModuleVideo | moduleVideoId, moduleId, videoId, trainingId, userId, position |
| DADOS-SV | SubjectVideo | subjectVideoId, subjectId, videoId, trainingId, position |
| DADOS-LINK | Link | linkId, videoId, name, url, position |

Não está em DADOS (e **não vai estar**): `familyId`, `locale`, `url` de YouTube, `thumbnail`, `password`, HTML de página de guru. Mídia: `videos/{videoId}.gif` + `videos/{videoId}.m4a`. Páginas: `gurus/{guruId}/{pageId}.html`.

## CONTRATO
**Revogado** (2026-08-28): prefixo `/course/v1` e `retrieve*ByCourseId*`. Equivalente Training abaixo.
Training: `/training/v1/create` (exige `guruId`) `retrieveModulesWithVideosByTrainingId` `retrieveModulesByTrainingId` `retrieveById` `retrieveSubjectsByTrainingId` `retrieveAll` (**top 9**, não lista completa) `retrieveByPerfil` `delete` (**hard**). JSON: `trainingId` (não `courseId`). **Não há** `/training/v1/update`.
Module: `/module/v1/create` `retrieveById` `retrieveByTrainingId` `retrieveByTrainingIdWithVideos` `update` `delete` `addVideo` `reorder` `removeVideo`
Subject: `/subject/v1/create` `retrieveByTrainingId` `retrieveByIdWithVideos` `retrieveByVideo` `update` `addVideo` `removeVideo`
Video: `/video/v1/create` `retrieveById` `retrieveBySubjectId` `update` `delete`
Link: `/link/v1/create` `retrieveByVideoId` `update` `delete`
Health: `/training/v1/healthCheck`

GuruPage (interno, leitura): listar por `guruId` (público na borda). Sem create/update público (NÃO-BACKOFFICE).
Não existe: `retrieveByLocale`. `retrieveByTrainingIdWithVideos` é o equivalente do antigo `retrieveByCourseIdWithVideos`. CRUD público de Guru **não** existe (NÃO-BACKOFFICE). Sem `retrieveByGuruId` no lançamento (só `java`; GAP-GURU-NAV no front).

## GAP
- GAP-LOCALE: **revogado** (2026-08-28). Idioma = i18n, não familyId no Training.
- GAP-GIF: **revogado** (2026-08-28). Campo `gif`. url/thumbnail saem.
- GAP-GURU-USER: **revogado** (2026-08-28). Aluno global.
- GAP-GURU-HOST: **revogado** (2026-08-28). Um domínio, um front.
- GAP-PERFIL-CHECK: `RequestService.checkIfThisPerfilBelongsToThisUser` sempre `false`; `retrieveByPerfil`/`delete` ignoram. Spec: consultar `perfil/v1/{id}/belongToUser/{userId}` e recusar se não pertencer.
- GAP-OWNER: dono (`Training.userId`) vale em retrieveById/delete/create de módulo/assunto. Retrieve/update de filho ainda não está fechado se deve checar dono.
- GAP-AUDIO: **revogado** (path fechado, `{videoId}.m4a`).
- GAP-COMPRA: **revogado** (MVP1). Entitlement no user MS (DADOS-COMPRA / DADOS-ASSINATURA). Ver SPEC-MVP1.md no frontend.
