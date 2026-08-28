<!-- para IA. não é README de humano. -->
# SPEC — course

status: v0
sha: `64b3b2f`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`INV-` `DADOS-` `END-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita INV/END. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra INV/NÃO; senão vira GAP e só então código.
- GAP = pergunta aberta. Não trate GAP como regra.

## Papel
MS **interno** (porta `8087`). Dono do conteúdo: Course → Module / Subject → Video → Link. Sem auth HTTP.

## INV
- INV-OWN-1: Course tem `userId` (dono). retrieveById/delete só do dono (código após PR #5).
- INV-OWN-2: Module/Subject/Video carregam `userId` (e courseId quando couber). Video/Course também têm `perfilId`.
- INV-TREE-1: Module pertence a um Course. Subject pertence a um Course. Video liga-se a Module via `ModuleVideo` (position) e/ou a Subject via `SubjectVideo`.
- INV-LINK-1: Link pertence a um Video (`videoId`, `name`, `url`, `position`).
- INV-DEL-1: Course/Subject/Video têm flag `deleted` nas **leituras**. O END `delete` de Course/Module/Video/Link hoje é **hard delete** (não flip da flag). Module/Link/joins não têm `deleted`.
- INV-ID-1: ids UUID.

## NÃO
- NÃO-EXPOSE
- NÃO-SHUTDOWN
- NÃO-I18: texto de UI não vive aqui (vai pro i18n). name/sinopse/description do conteúdo **sim**.
- NÃO-GIF-FIELD: tabela Video **não** tem coluna `gif`. Tem `url` e `thumbnail`. Front que espera `video.gif` é GAP-FRONT, não campo novo automático.

## DADOS
| id | tabela | campos |
|---|---|---|
| DADOS-COURSE | Course | courseId, userId, perfilId, name, sinopse, description, deleted |
| DADOS-MOD | Module | moduleId, courseId, userId, name, sinopse, number, newNumber, description |
| DADOS-SUB | Subject | subjectId, courseId, userId, name, sinopse, description, deleted |
| DADOS-VID | Video | videoId, userId, perfilId, name, sinopse, description, url, thumbnail, deleted |
| DADOS-MV | ModuleVideo | moduleVideoId, moduleId, videoId, courseId, userId, position |
| DADOS-SV | SubjectVideo | subjectVideoId, subjectId, videoId, courseId, position |
| DADOS-LINK | Link | linkId, videoId, name, url, position |

Não está em DADOS (front usa, DB não tem): `familyId`, `locale`, `gif`, `trainingId`.

## END
Course: `/course/v1/create` `retrieveModulesWithVideosByCourseId` `retrieveModulesByCourseId` `retrieveById` `retrieveSubjectsByCourseId` `retrieveAll` (**top 9**, não lista completa) `retrieveByPerfil` `delete` (**hard**). **Não há** `/course/v1/update`.
Module: `/module/v1/create` `retrieveById` `retrieveByCourseId` `retrieveByCourseIdWithVideos` `update` `delete` `addVideo` `reorder` `removeVideo`
Subject: `/subject/v1/create` `retrieveByCourseId` `retrieveByIdWithVideos` `retrieveByVideo` `update` `addVideo` `removeVideo`
Video: `/video/v1/create` `retrieveById` `retrieveBySubjectId` `update` `delete`
Link: `/link/v1/create` `retrieveByVideoId` `update` `delete`
Health: `/course/v1/healthCheck`

Não existe: `retrieveByLocale`, `retrieveByTrainingIdWithVideos`.

## GAP
- GAP-LOCALE: front pede curso por locale/familyId. Spec de produto: curso é por idioma (N linhas) ou i18 cobre os textos?
- GAP-PERFIL-CHECK: `RequestService.checkIfThisPerfilBelongsToThisUser` sempre `false`; `retrieveByPerfil`/`delete` ignoram. Spec: deve consultar `perfil/v1/{id}/belongToUser/{userId}` e recusar se não pertencer.
- GAP-GIF: mídia da vitrine é `buckets/digitus-forum-media/videos/{id}.gif`. Isso é o campo `url`, `thumbnail`, ou um campo novo `gif`? Sem GAP fechado, **não adicionar coluna**.
- GAP-OWNER: dono (`Course.userId`) vale em retrieveById/delete/create de módulo/assunto. A maior parte de retrieve/update de filho **não** checa dono — spec de produto ainda não diz se isso é regra ou buraco.
