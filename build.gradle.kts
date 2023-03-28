allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }

    create<Delete>("clean") {
        delete(rootProject.buildDir)
    }
}
