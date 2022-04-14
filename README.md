# projecto_ppm

## Início
Para sacar o código inicialmente quando criarem um novo projecto no intelij usem a hipótese "Get from VCS" e depois colar o url deste repositório, que se encontra no gitHub.

Se estiverem a usar o gitBash é executar `git clone https://github.com/guimmn2/projecto_ppm.git` sendo que já são colaboradores.

*ATENÇÃO* : Possívelmente terão que configurar o git do intelij e associar o vosso email da conta para poderem fazer agir sobre o repositório.

## Branches
Durante o desenvolvimento teremos 2 + n branches:
- *main*: Este será o branch onde colocaremos as versões finais para apresentação (snapshots) conforme vamos avançando.
- *development*: Este será o branch onde vamos buscar o código actualizado para os nossos desenvolvimentos e para onde, primeiramente, enviaremos as nossas alterações.
- *feature/x*: Estes serão os n branches que vamos criando conforme fazemos desenvolvimentos, em que x será a designação do desenvolvimento.

## Workflow
Depois de terem configurado o git e clonado o repositório podem começar a desenvolver. Para isto, estando no branch "development" corram o comando `git pull` e criem um branch com o nome "feature/<nome do desenvolvimento>", isto vai fazer com que seja criado um branch com o código que estava no branch "development".

Já tendo o branch da feature criada, sempre antes de começarem a trabalhar, para se certificarem que têm a versão mais recente do branch "development", façam *merge* do "development" para o vosso branch "feature/x" correndo o seguinte comando: `git merge development`

Conforme forem trabalhando no vosso branch, à medida que vão avançando devem fazer "commit" das vossas alterações e enviá-las para o repositório fazendo "push", desta forma podemos ver o histórico de desenvolvimento das features uns dos outros. Para fazer "commit" devem correr os seguintes comandos: 
- `git add <path>`: onde o <path> é onde se encontram os ficheiros ou o próprio ficheiro para serem "commited".
- `git commit -m "<mensagem descritiva do desenvolvimento>"`: grava um snapshot com essas alterações.
- `git push`: envia as alterações "commited" para o repositório.
