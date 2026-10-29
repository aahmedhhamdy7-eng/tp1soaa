package tn.fss;

import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

    public static void main(String[] args) {

        try {

            // ============ ETAPE 4 : VALIDATION XML / XSD ============

            SchemaFactory factory = SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // 1) Validation du fichier valide
            try (InputStream xsd =
                         Main.class.getResourceAsStream("/etudiant.xsd");
                 InputStream xml =
                         Main.class.getResourceAsStream("/etudiant.xml")) {

                if (xsd == null || xml == null) {
                    throw new Exception(
                            "Fichier XML ou XSD introuvable !");
                }

                Schema schema = factory.newSchema(
                        new StreamSource(xsd));

                Validator validator = schema.newValidator();

                validator.validate(new StreamSource(xml));

                System.out.println("etudiant.xml : XML valide !");
            }

            // 2) Validation du fichier non valide (test)
            try (InputStream xsd =
                         Main.class.getResourceAsStream("/etudiant.xsd");
                 InputStream xml =
                         Main.class.getResourceAsStream("/invalid.xml")) {

                if (xsd == null || xml == null) {
                    throw new Exception(
                            "Fichier XML invalide ou XSD introuvable !");
                }

                Schema schema = factory.newSchema(
                        new StreamSource(xsd));

                Validator validator = schema.newValidator();

                try {
                    validator.validate(new StreamSource(xml));
                    System.out.println("invalid.xml : XML valide !");
                } catch (SAXException e) {
                    System.out.println(
                            "invalid.xml : XML NON valide !");
                    System.out.println("   Cause : " + e.getMessage());
                }
            }

            // ============ ETAPE 5 : INTERROGATION XPATH ============

            DocumentBuilderFactory dbFactory =
                    DocumentBuilderFactory.newInstance();

            Document document;

            try (InputStream xml =
                         Main.class.getResourceAsStream("/etudiant.xml")) {

                if (xml == null) {
                    throw new Exception("etudiant.xml introuvable !");
                }

                document = dbFactory.newDocumentBuilder().parse(xml);
            }

            XPath xpath = XPathFactory.newInstance().newXPath();


            // 1) Tous les etudiants
            XPathExpression exp1 = xpath.compile("//etudiant");
            NodeList resultat1 = (NodeList)
                    exp1.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nTous les etudiants :");

            for (int i = 0; i < resultat1.getLength(); i++) {
                Node etudiant = resultat1.item(i);
                System.out.println(
                        xpath.evaluate("prenom", etudiant) + " "
                                + xpath.evaluate("nom", etudiant));
            }


            // 2) Etudiants de la filiere LSI
            XPathExpression exp2 =
                    xpath.compile("//etudiant[filiere='LSI']");
            NodeList resultat2 = (NodeList)
                    exp2.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nEtudiants de la filiere LSI :");

            for (int i = 0; i < resultat2.getLength(); i++) {
                Node etudiant = resultat2.item(i);
                System.out.println(
                        xpath.evaluate("prenom", etudiant) + " "
                                + xpath.evaluate("nom", etudiant)
                                + " (niveau " + xpath.evaluate("niveau", etudiant) + ")");
            }


            // 3) Etudiants ayant une moyenne >= 15
            XPathExpression exp3 =
                    xpath.compile("//etudiant[moyenne >= 15]");
            NodeList resultat3 = (NodeList)
                    exp3.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nEtudiants ayant une moyenne >= 15 :");

            for (int i = 0; i < resultat3.getLength(); i++) {
                Node etudiant = resultat3.item(i);
                System.out.println(
                        xpath.evaluate("prenom", etudiant) + " "
                                + xpath.evaluate("nom", etudiant)
                                + " - moyenne : " + xpath.evaluate("moyenne", etudiant));
            }


            // 4) Etudiant avec l'identifiant E001
            XPathExpression exp4 =
                    xpath.compile("//etudiant[@id='E001']");
            NodeList resultat4 = (NodeList)
                    exp4.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nEtudiant avec l'identifiant E001 :");

            for (int i = 0; i < resultat4.getLength(); i++) {
                Node etudiant = resultat4.item(i);
                System.out.println(
                        xpath.evaluate("prenom", etudiant) + " "
                                + xpath.evaluate("nom", etudiant)
                                + " - " + xpath.evaluate("email", etudiant));
            }


            // 5) Matieres enseignees par l'enseignant P01
            XPathExpression exp5 =
                    xpath.compile("//matiere[enseignantRef='P01']/intitule");
            NodeList resultat5 = (NodeList)
                    exp5.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nMatieres enseignees par P01 :");

            for (int i = 0; i < resultat5.getLength(); i++) {
                System.out.println(resultat5.item(i).getTextContent());
            }


            // 6) Enseignants du departement Informatique
            XPathExpression exp6 = xpath.compile(
                    "//enseignant[departementRef = //departement[nom='Informatique']/@id]");
            NodeList resultat6 = (NodeList)
                    exp6.evaluate(document, XPathConstants.NODESET);

            System.out.println("\nEnseignants du departement Informatique :");

            for (int i = 0; i < resultat6.getLength(); i++) {
                Node enseignant = resultat6.item(i);
                System.out.println(
                        xpath.evaluate("prenom", enseignant) + " "
                                + xpath.evaluate("nom", enseignant)
                                + " - " + xpath.evaluate("grade", enseignant));
            }


            // 7) Nombre d'etudiants et moyenne generale
            double nombre = (Double) xpath.compile("count(//etudiant)")
                    .evaluate(document, XPathConstants.NUMBER);
            double moyenne = (Double) xpath.compile(
                            "sum(//etudiant/moyenne) div count(//etudiant)")
                    .evaluate(document, XPathConstants.NUMBER);

            System.out.println("\nStatistiques :");
            System.out.printf("Nombre d'etudiants : %.0f%n", nombre);
            System.out.printf("Moyenne generale : %.2f%n", moyenne);

        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}