$Host.UI.RawUI.WindowTitle = "Postgres Init"
mvn compile exec:java -D"exec.mainClass"="at.ac.tuwien.complang.vpsbcm.robnur.postgres.InitDb"