<!-- para IA. não é README de humano. -->
# SPEC — course

status: v0.2
sha: `64b3b2f`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`REGRA-` `DADOS-` `CONTRATO-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita REGRA/CONTRATO. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra REGRA/NÃO; senão vira GAP e só então código.
- GAP = pergunta aberta. Não trate GAP como regra.

## Papel
MS **interno** (porta `8087`). Dono do conteúdo: Course → Module / Subject → Video → Link. Sem auth HTTP.

## REGRA
- REGRA-OWN-1: Course tem `userId` (dono). retrieveById/delete só do dono (código após PR #5).
- REGRA-OWN-2: Module/Subject/Video carregam `userId` (e courseId quando couber). Video/Course também têm `perfilId`.
- REGRA-TREE-1: Module pertence a um Course. Subject pertence a um Course. Video liga-se a Module via `ModuleVideo` (position) e/ou a Subject via `SubjectVideo`.
- REGRA-LINK-1: Link pertence a um Video (`videoId`, `name`, `url`, `position`).
- REGRA-DEL-1: Course/Subject/Video têm flag `deleted` nas **leituras**. O CONTRATO `delete` de Course/Module/Video/Link hoje é **hard delete** (não flip da flag). Module/Link/joins não têm `deleted`.
- REGRA-ID-1: ids UUID.

## NÃO
- NÃO-EXPOSE
- NÃO-SHUTDOWN
- NÃO-I18: texto de UI e variação pt/en **não** vivem aqui (vão pro i18n). Course não duplica por idioma.
- NÃO-LOCALE: Course **não** tem `locale` nem `familyId`.
- NÃO-YOUTUBE: `url` e `thumbnail` em Video são legado (YouTube). **Remover.** Aula = `gif` (+ áudio).

## DADOS
| id | tabela | campos |
|---|---|---|
| DADOS-COURSE | Course | courseId, userId, perfilId, name, sinopse, description, paid (false = gratuito), deleted |
| DADOS-MOD | Module | moduleId, courseId, userId, name, sinopse, number, newNumber, description |
| DADOS-SUB | Subject | subjectId, courseId, userId, name, sinopse, description, deleted |
| DADOS-VID | Video | videoId, userId, perfilId, name, sinopse, description, gif, deleted. **Não** url/thumbnail. Áudio: GAP-AUDIO. |
| DADOS-MV | ModuleVideo | moduleVideoId, moduleId, videoId, courseId, userId, position |
| DADOS-SV | SubjectVideo | subjectVideoId, subjectId, videoId, courseId, position |
| DADOS-LINK | Link | linkId, videoId, name, url, position |

Não está em DADOS (e **não vai estar**): `familyId`, `locale`, `url` de YouTube, `thumbnail`. Chave de mídia: `buckets/digitus-forum-media/videos/{videoId}.gif`.

## CONTRATO
Course: `/course/v1/create` `retrieveModulesWithVideosByCourseId` `retrieveModulesByCourseId` `retrieveById` `retrieveSubjectsByCourseId` `retrieveAll` (**top 9**, não lista completa) `retrieveByPerfil` `delete` (**hard**). **Não há** `/course/v1/update`.
Module: `/module/v1/create` `retrieveById` `retrieveByCourseId` `retrieveByCourseIdWithVideos` `update` `delete` `addVideo` `reorder` `removeVideo`
Subject: `/subject/v1/create` `retrieveByCourseId` `retrieveByIdWithVideos` `retrieveByVideo` `update` `addVideo` `removeVideo`
Video: `/video/v1/create` `retrieveById` `retrieveBySubjectId` `update` `delete`
Link: `/link/v1/create` `retrieveByVideoId` `update` `delete`
Health: `/course/v1/healthCheck`

Não existe: `retrieveByLocale`, `retrieveByTrainingIdWithVideos`.

## GAP
- GAP-LOCALE: **revogado** (2026-08-28). Idioma = i18n, não familyId no Course.
- GAP-GIF: **revogado** (2026-08-28). Campo `gif`. url/thumbnail saem.
- GAP-PERFIL-CHECK: `RequestService.checkIfThisPerfilBelongsToThisUser` sempre `false`; `retrieveByPerfil`/`delete` ignoram. Spec: consultar `perfil/v1/{id}/belongToUser/{userId}` e recusar se não pertencer.
- GAP-OWNER: dono (`Course.userId`) vale em retrieveById/delete/create de módulo/assunto. Retrieve/update de filho ainda não está fechado se deve checar dono.
- GAP-AUDIO: arquivo de áudio da aula (coluna vs convenção de path).
