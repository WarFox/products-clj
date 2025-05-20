# Products App - Full Stack Clojure/ClojureScript

This is a full stack Clojure/ClojureScript application that allows users to
manage products. The app is built using the following technologies:

- Backend: Clojure - [Ring](https://github.com/ring-clojure/ring),
  [Reitit](https://github.com/metosin/reitit),
  [next.jdbc](https://github.com/seancorfield/next-jdbc/),
  [Integrant](https;//github.com/weavejester/integrant), [Muuntaja](https://cljdoc.org/d/metosin/muuntaja/)
- Frontend: ClojureScript - [re-frame](https://day8.github.io/re-frame/),
  [Reagent](https://reagent-project.github.io/), [react](https://react.dev/), [tailwindcss](https://tailwindcss.com/)
- Shared: [Malli](https://cljdoc.org/d/metosin/malli/) Schemas
- Database: [PosgreSQL](https://www.postgresql.org/), [Flyway](https://flywaydb.org/) for Migrations
- Testing: Test Containers using [testcontainers-clj](https://github.com/testcontainers/testcontainers-clj)

![Products](https://gist.githubusercontent.com/WarFox/91fff34911d1080a66723770cb12c4e7/raw/ec78e55cb4f834fd3e4ea1c57f0ee521f9120384/products-app.png)

## Installation

```
git clone git@github.com:WarFox/products-clj
```

## Run the project for dev

1. Start the Backend

```shell
lein run
```

This starts the backend at <http://localhost:3000/>, it also starts a postgres
database in a container. Docker is needed for this to run.

You may also start the backend using repl using `(go)` function in
`dev/user.clj` file.

2. Start the UI

``` shell
  cd ui
  npm run watch
```

This starts the frontend at <http://localhost:8280/> in watch mode for development

3. Start tailwind process

``` shell
  cd ui
  npm run watch:css
```

## License

Copyright © 2025 Deepu Mohan Puthrote

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
<http://www.eclipse.org/legal/epl-2.0>.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at <https://www.gnu.org/software/classpath/license.html>.
