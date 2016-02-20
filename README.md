# Ward

Ward is a simple library that enables runtime modularity for tc Server web applications. It's built on top of the
Spring Framework and offers a lightweight alternative for scenarios that otherwise require an OSGi container.

## Quickstart

Download and examine the structure of the provided sample. Do one of the following:

* Import as Maven Projects in STS and use the built-in tooling to deploy them in a tc Server instance which has a
customized catalina.properties configuration with an extra common.loader entry pointing to
<your-local-path>/samples/intro/assembly/target/lib/*.jar
* Build everything, create an environment variable CATALINA_HOME which points to a Tomcat distribution and use the
start script in assembly/target/bin

Point your browser to http://localhost:8080/ward and examine how the application behaves when projects are added
or removed

## TODO

* Consider renaming classes from Application* to Module*
* Introduce application groups
* Implement a concurrent registry map
* Integration with the Live Beans view
* Enable nested ward:service beans

## License

Ward is released under the [Apache 2.0 License](LICENSE).
