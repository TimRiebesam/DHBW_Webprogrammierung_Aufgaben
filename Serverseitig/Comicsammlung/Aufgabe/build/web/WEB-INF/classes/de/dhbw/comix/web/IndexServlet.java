package de.dhbw.comix.web;

import de.dhbw.comix.database.DatabaseFacade;
import de.dhbw.comix.database.Serie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Requenst Handler für die Startseite mit folgenden Funktionen:
 * 
 *   * Anzeige aller vorhandenen Comics
 *   * Anlage eines neuen Comics
 *   * Löschen aller ausgewählten Comics
 */
@WebServlet(urlPatterns={"/index.html"})
public class IndexServlet extends HttpServlet {
    
    @EJB DatabaseFacade database;
    
    /**
     * GET-Anfrage: Alle vorhandenen Comics anzeigen
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        //Serien aus Datenbank holen
        List<Serie> serien = this.database.getAllComics();
        
        //Attribut ersetzen mit Inhalt aus Datenbank
        request.setAttribute("serien", serien);
        
        //Weiterleitung an Template:
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        
        //Eingaben entfernen
        HttpSession session = request.getSession();
        session.removeAttribute("fehlermeldungen");
        session.removeAttribute("serie");
        session.removeAttribute("nummer");
        session.removeAttribute("titel");
        session.removeAttribute("zeichner");
        session.removeAttribute("jahr");
        session.removeAttribute("texter");
    }
    
    /**
     * POST-Anfrage: Neuen Comic anlegen oder Comics löschen
     * 
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException 
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        String aktion = request.getParameter("aktion");
        
        switch(aktion){
            case "anlegen":
                //Neuen Comic anlegen
                this.handleAnlegen(request);
                break;
            case "loeschen":
                //Comic(s) loeschen
                this.handleLoeschen();
                break;
        }
        response.sendRedirect(request.getRequestURI());
    }
    
    public void handleAnlegen(HttpServletRequest request) throws IOException, ServletException{
        String serie = request.getParameter("serie");
        String nummer = request.getParameter("nummer");
        String jahr = request.getParameter("jahr");
        String titel = request.getParameter("titel");
        String texter = request.getParameter("texter");
        String zeichner = request.getParameter("zeichner");
        
        List<String> fehlermeldungen = new ArrayList();
        
        if(serie == null || serie.isEmpty()){
            fehlermeldungen.add("Die Serie muss einen Seriennamen besitzen!");
        }
        
        if(nummer == null || nummer.isEmpty()){
            fehlermeldungen.add("Die Nummer darf nicht leer sein und darf ausschließlich aus Zahlen bestehen!");
        }
        
        if(jahr == null || jahr.isEmpty()){
            fehlermeldungen.add("Das Jahr darf nicht leer sein und darf ausschließlich aus Zahlen bestehen!");
        }
        
        if(titel == null || titel.isEmpty()){
            fehlermeldungen.add("Der Titel darf nicht leer sein!");
        }
        
        if(texter == null || texter.isEmpty()){
            fehlermeldungen.add("Der Texter muss angegeben werden!");
        }
        
        if(zeichner == null || zeichner.isEmpty()){
            fehlermeldungen.add("Der Zeichner muss angegeben werden!");
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("fehlermeldungen", fehlermeldungen);
        
        if(fehlermeldungen.isEmpty()){
            //Kein Fehler: Neues Comic anlegen
            this.database.createNewComic(serie, titel, new Integer(nummer), new Integer(jahr), zeichner, texter);
        }
        else{
            //Eingaben wieder in Formular füllen und Fehler ausgeben
            session.setAttribute("serie", serie);
            session.setAttribute("titel", titel);
            session.setAttribute("nummer", nummer);
            session.setAttribute("jahr", jahr);
            session.setAttribute("zeichner", zeichner);
            session.setAttribute("texter", texter);
        }
        
    }
    
    public void handleLoeschen() throws IOException, ServletException{
        
    }
    
}
