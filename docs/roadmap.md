---
layout: default
---

# Roadmap
 
![roadmap](http://manifold.systems/images/roadmap.jpg)
 
## Recently Completed

Manifold gained a lot of ground in recent months. Notably, the structure of the Manifold framework changed to support compile-only
usage where possible. Since much of Manifold operates exclusively within the Java compiler, the changes amount to
separating purely compile-time behavior from runtime behavior by introducing new runtime modules ending with *-rt*. As a result you
can limit compile-time Manifold usage to the annotation processor path and completely avoid runtime dependencies, which significantly
reduces your project's Manifold footprint. Big win!

>Note, revised build instructions are available in the `readme.md` files.

This restructure was primarily motivated by a couple of big, long awaited features, namely [Android Studio support](http://manifold.systems/android.html)
and [Kotlin support](http://manifold.systems/kotlin.html), which are now available for use. Manifold now fully supports Android
applications and Android Studio. Additionally, type manifolds such as [GraphQL](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-graphql),
[JSON](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-json),
[XML](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-xml),
[CSV](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-csv), etc. fully support
Kotlin and other JVM language applications.

More recently the following features were implemented:
- [#204](https://github.com/manifold-systems/manifold/issues/204) - support relational expressions in the preprocessor
- [#198](https://github.com/manifold-systems/manifold/issues/198) - support *--release 8* (or lower) from JDK 9+
- [#189](https://github.com/manifold-systems/manifold/issues/189) - support [Array type extensions](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext#extending-arrays)
- Support for [explicit resource compilation](https://github.com/manifold-systems/manifold/tree/master/manifold-core-parent/manifold#explicit-resource-compilation)
via javac command line argument to specify additional source paths: *-Amanifold.source=<paths>*.

  
## On the workbench

 I haven't quite committed to anything big at the moment. For now I'm reviewing bugs and looking for small stuff
 to fortify existing features until I figure out what is next. Visit the Manifold [Slack group](https://join.slack.com/t/manifold-group/shared_invite/zt-e0bq8xtu-93ASQa~a8qe0KDhOoD6Bgg)
 and give a shout if you have a preference.
 
## On the pile (in no particular order)

#### Extension method specialization 
* [207](https://github.com/manifold-systems/manifold/issues/207)

#### Operator overloading enhancements
* [#126](https://github.com/manifold-systems/manifold/issues/126)

#### New schema type manifolds 
* [#111](https://github.com/manifold-systems/manifold/issues/111)

#### Manifold "inliner" (aka De-Manifold) tool
* [#95](https://github.com/manifold-systems/manifold/issues/95)

#### Default parameter values
* [#93](https://github.com/manifold-systems/manifold/issues/93)

#### Manifold plugin for VS Code (which is Eclipse)
* [#142](https://github.com/manifold-systems/manifold/issues/142)

#### Manifold plugin for Eclipse
* [#18](https://github.com/manifold-systems/manifold/issues/18)

