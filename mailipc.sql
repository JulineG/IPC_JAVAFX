-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le :  mer. 18 mars 2020 à 14:36
-- Version du serveur :  10.1.37-MariaDB
-- Version de PHP :  7.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `mailipc`
--
CREATE DATABASE IF NOT EXISTS `mailipc` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `mailipc`;

-- --------------------------------------------------------

--
-- Structure de la table `mail`
--

CREATE TABLE `mail` (
  `idMail` int(11) NOT NULL,
  `refUserSrc` int(11) NOT NULL,
  `refUserDst` int(11) NOT NULL,
  `objet` varchar(100) NOT NULL,
  `corps` varchar(500) NOT NULL,
  `date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isDeleting` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `mail`
--

INSERT INTO `mail` (`idMail`, `refUserSrc`, `refUserDst`, `objet`, `corps`, `date`, `isDeleting`) VALUES
(1, 3, 1, 'Cours IPC', 'J\'adore les cours de IPC et toi?', '2020-03-09 10:27:54', 0),
(2, 1, 3, 'Re - Cours IPC', 'Je kiff aussi bg', '2020-03-09 10:27:54', 0),
(3, 2, 1, 'Cantine', 'On mange au RU ou au mcdo? j\'ai trop faim comme dab, vive l\'obésité', '2020-03-09 10:27:54', 0),
(4, 3, 1, 'Petite dissertation', 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.', '2020-03-18 11:30:31', 0);

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `idUser` int(11) NOT NULL,
  `login` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`idUser`, `login`, `password`) VALUES
(1, 'LucasA', 'secret'),
(2, 'JulineG', 'secret'),
(3, 'YohanM', 'secret');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `mail`
--
ALTER TABLE `mail`
  ADD PRIMARY KEY (`idMail`),
  ADD KEY `fk_mail_user_src` (`refUserSrc`),
  ADD KEY `fk_mail_user_dst` (`refUserDst`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`idUser`),
  ADD UNIQUE KEY `u_login` (`login`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `mail`
--
ALTER TABLE `mail`
  MODIFY `idMail` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `idUser` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `mail`
--
ALTER TABLE `mail`
  ADD CONSTRAINT `fk_mail_user_dst` FOREIGN KEY (`refUserDst`) REFERENCES `user` (`idUser`),
  ADD CONSTRAINT `fk_mail_user_src` FOREIGN KEY (`refUserSrc`) REFERENCES `user` (`idUser`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
