# Howto Release

## Perform a release

Release are uploaded to a staging repository of [Maven Central](https://search.maven.org):

```bash
# perform release (use `git reset --hard origin/master` and delete tags to retry)
mvn release:clean
mvn release:prepare
mvn release:perform
```

Then visit [oss.sonatype.org](https://oss.sonatype.org) in order to deploy the release.

It takes up to 2 hours until the release appears at [Maven Central](https://search.maven.org/#search%7Cga%7C1%7Cvavr-beanvalidation2). The public repository can be found [here](https://repo.maven.apache.org/maven2/io/vavr/vavr-beanvalidation2).

## Best practice: Remove an existing release from Git

Sometime something goes wrong during a release. Here are some steps to restore the previous state.

Given a **&lt;version>** (e.g. 0.9.2), a _non-published_ release can be removed like this:

```bash
# get actual master
git clone https://github.com/vavr-io/vavr-beanvalidation2.git ; vavr-beanvalidation2

# delete existing tag (local and remote)
git tag -d v<version>
git push --delete origin v<version>

# revert development version
mvn versions:set -DnewVersion=<version>-SNAPSHOT
git commit -a -m "Reverted to <version>-SNAPSHOT"
git push origin master ; git pull
```
